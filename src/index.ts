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

dotenv.config();

const storage = multer.diskStorage({
    destination: "./uploads/",
    filename: (req, file, cb) => {
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
        context: ({ req }) => {
            const token = req.headers.authorization?.split(" ")[1] || "";
            const user = verifyJWT(token);
            return { user };
        },
    });

    await server.start();
    server.applyMiddleware({ app });

    initWebSocket(httpServer);

    app.get("/", (req, res) => {
        res.send("Hello Cochiwawa backend");
    });

    // Image Upload Endpoint
    app.post("/api/upload", upload.single("image"), (req: Request, res: Response) => {
        if (!req.file) return res.status(400).json({ error: "No file uploaded" });
        const url = `http://10.0.2.2:${PORT}/uploads/${req.file.filename}`;
        res.json({ url });
    });

    // REST API for Rides
    app.get("/api/rides", async (req: Request, res: Response) => {
        const origin = req.query.origin as string || "";
        const destination = req.query.destination as string || "";
        try {
            const rides = await RideModel.search(origin, destination);
            res.json(rides);
        } catch (error) {
            res.status(500).json({ error: "Internal Server Error" });
        }
    });

    httpServer.listen(PORT, () => {
        console.log(`Cochiwawa backend is running on http://localhost:${PORT}`);
        console.log(`GraphQL endpoint: http://localhost:${PORT}${server.graphqlPath}`);
        console.log(`WebSocket server is ready`);
    });
}

startServer();
