import express, { Express, Request, Response } from "express";
import { ApolloServer } from "apollo-server-express";
import dotenv from "dotenv";
import { createServer } from "http";
import multer from "multer";
import path from "path";
import { RideController } from "./domain/ride/ride.controller";
import { RideModel } from "./domain/ride/ride.model";
import { typeDefs } from "./graphql/schema";
import { resolvers } from "./graphql/resolvers";
import { verifyJWT } from "./services/authService";
import { initWebSocket } from "./websocket/WebSocketServer";
import chatRoutes from "./routes/chatRoutes";
import reputationRoutes from "./routes/reputationRoutes";
import emergencyRoutes from "./routes/emergencyRoutes";

dotenv.config();

const storage = multer.diskStorage({
    destination: "./uploads/",
    filename: (req: any, file: any, cb: any) => {
        cb(null, `${Date.now()}-${file.originalname}`);
    }
});
const upload = multer({ storage });

async function startServer() {
    const app: Express = express();
    app.use(express.json());
    app.use("/uploads", express.static("uploads"));

    const PORT = process.env.PORT || 5000;
    const httpServer = createServer(app);

    const server = new ApolloServer({
        typeDefs,
        resolvers,
        context: ({ req }: { req: any }) => {
            const token = req.headers.authorization?.split(" ")[1] || "";
            const user = verifyJWT(token);
            return { user };
        },
    });

    await server.start();
    server.applyMiddleware({ app: app as any });

    initWebSocket(httpServer);

    // Register routes
    app.use("/chat", chatRoutes);
    app.use("/api", reputationRoutes);
    app.use("/api", emergencyRoutes);

    app.get("/", (req, res) => {
        res.json({
            message: "Cochiwawa Backend - Carpool Safety & Reputation System",
            version: "3.0.0",
            features: [
                "Route Matching Engine",
                "Seat Reservation Protection",
                "Trust & Reputation System",
                "Trust Badges",
                "Trip Completion",
                "Emergency Alert System",
                "Chat for Confirmed Bookings",
                "Identity Verification",
                "Vehicle Verification"
            ]
        });
    });

    // Image Upload Endpoint
    app.post("/api/upload", upload.single("image"), (req: Request, res: Response) => {
        const file = (req as any).file as any;
        if (!file) return res.status(400).json({ error: "No file uploaded" });
        const url = `http://10.0.2.2:${PORT}/uploads/${file.filename}`;
        res.json({ url });
    });

    // REST API for Rides - Updated with route matching
    app.get("/api/rides", RideController.searchRides);

    // Trip completion endpoint
    app.post("/api/trips/:tripId/complete", RideController.completeTrip);

    // Get driver trips
    app.get("/api/drivers/:driverId/trips", RideController.getDriverTrips);

    // Get active trips
    app.get("/api/trips/active", RideController.getActiveTrips);

    httpServer.listen(PORT, () => {
        console.log(`Cochiwawa backend is running on http://localhost:${PORT}`);
        console.log(`GraphQL endpoint: http://localhost:${PORT}${server.graphqlPath}`);
        console.log(`WebSocket server is ready`);
        console.log(`
╔══════════════════════════════════════════════════════════════╗
║      Cochiwawa Backend - Safety & Reputation System          ║
║                          v3.0.0                               ║
╚══════════════════════════════════════════════════════════════╝

API Endpoints:
  Rides:         GET    /api/rides (with route matching)
  Trip Complete: POST   /api/trips/:tripId/complete
  Reputation:    GET    /api/users/:id/reputation
  Reviews:       POST   /api/reviews
  Emergency:     POST   /api/emergency-alert
  Chat:          GET    /chat/rooms/:userId

New Features:
  ✓ Route Matching Engine (geographic proximity)
  ✓ Seat Reservation Protection (transactions)
  ✓ Trust & Reputation System
  ✓ Trust Badges (verification flags)
  ✓ Trip Completion & Review Unlock
  ✓ Emergency Alert System
  ✓ Chat for Confirmed Bookings
  ✓ Identity & Vehicle Verification

Future Ready:
  • WebSocket real-time chat
  • Push notifications
  • Real-time ride tracking
        `);
    });
}

startServer();
