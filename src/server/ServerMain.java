package server;

import server.view.ServerUi;

public class ServerMain {
    public static void main(String[] args) {
        ServerUi view = new ServerUi();
        server.controller.ServerController controller = new server.controller.ServerController(view);
        controller.showUI();
    }
}