package server.actions;

import common.Announcement;
import org.xml.sax.SAXException;
import server.Server;
import server.components.XMLHandler;

import javax.xml.parsers.ParserConfigurationException;
import java.util.Arrays;
import java.io.IOException;
import java.util.Date;

public class AnnouncementActions {

    public static Object[] postAnnouncement(Announcement announcement, int clientId) {
        try {
            if (announcement == null) {
                return new Object[]{"400", "Announcement payload is required", null};
            }
            if (announcement.getTitle() == null || announcement.getTitle().trim().isEmpty()) {
                return new Object[]{"400", "Announcement title is required", null};
            }
            if (announcement.getMessage() == null || announcement.getMessage().trim().isEmpty()) {
                return new Object[]{"400", "Announcement message is required", null};
            }

            XMLHandler<Announcement> handler = new XMLHandler<>(
                    Announcement.class,
                    "src/server/data/database.xml",
                    "announcement"
            );

            Announcement[] current = handler.ReadAll();
            if (current != null && current.length >= 3) {
                Announcement oldest = Arrays.stream(current)
                        .filter(a -> a != null)
                        .min((a, b) -> {
                            Date da = a.getPosted_at();
                            Date db = b.getPosted_at();
                            if (da == null && db == null) return Integer.compare(a.getAttr_id(), b.getAttr_id());
                            if (da == null) return -1;
                            if (db == null) return 1;
                            int cmp = da.compareTo(db);
                            return cmp != 0 ? cmp : Integer.compare(a.getAttr_id(), b.getAttr_id());
                        })
                        .orElse(null);

                if (oldest != null) {
                    handler.Delete("id", oldest.getAttr_id());
                    current = handler.ReadAll();
                }
            }

            int newId = 1;
            if (current != null) {
                for (Announcement existing : current) {
                    if (existing != null) {
                        newId = Math.max(newId, existing.getAttr_id() + 1);
                    }
                }
            }
            announcement.setAttr_id(newId);

            if (announcement.getPosted_at() == null) {
                announcement.setPosted_at(new Date());
            }

            handler.Store("announcement", announcement);
            handler.saveToXML();

            Server.logEvent(
                    "Client " + clientId,
                    "Post Announcement",
                    "Announcement #" + newId + " posted"
            );            return new Object[]{"200", "Announcement posted successfully", announcement};
        } catch (ParserConfigurationException | IOException | SAXException e) {
            return new Object[]{"500", "Server error: " + e.getMessage(), null};
        }
    }

    public static Object[] getAllAnnouncements() {
        try {
            XMLHandler<Announcement> handler = new XMLHandler<>(
                    Announcement.class,
                    "src/server/data/database.xml",
                    "announcement"
            );
            Announcement[] announcements = handler.ReadAll();
            return new Object[]{"200", "Announcements retrieved successfully", announcements};
        } catch (ParserConfigurationException | IOException | SAXException e) {
            return new Object[]{"500", "Server error: " + e.getMessage(), null};
        }
    }
}
