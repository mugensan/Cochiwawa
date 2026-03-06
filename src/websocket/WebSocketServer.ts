import WebSocket, { Server as WebSocketServer } from "ws";
import { Server } from "http";

export let wss: WebSocketServer;

export function initWebSocket(server: Server) {
    wss = new WebSocketServer({ server });

    wss.on("connection", (ws: WebSocket) => {
        console.log("Client connected to WebSocket");

        ws.send(JSON.stringify({
            type: "CONNECTED",
            message: "Welcome to Cochiwawa Real-time updates"
        }));

        ws.on("close", () => {
            console.log("Client disconnected");
        });
    });
}

export function broadcast(data: any) {
    if (!wss) return;
    wss.clients.forEach((client: WebSocket) => {
        if (client.readyState === WebSocket.OPEN) {
            client.send(JSON.stringify(data));
        }
    });
}
