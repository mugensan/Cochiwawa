import express, { Express } from "express";
import dotenv from "dotenv";
import { postgraphile } from "postgraphile";

dotenv.config();

const app: Express = express();
const PORT = process.env.PORT || 5000;

// PostGraphile middleware
app.use(
    postgraphile(
        process.env.DATABASE_URL,
        {
            watchPg: true,
            graphiql: true,
            enhanceGraphiql: true,
            pgDefaultRole: "postgres",
        }
    )
);

app.get("/", (req, res) => {
    res.send("Hello Cochiwawa backend");
});

app.listen(PORT, () => {
    console.log(`Cochiwawa backend is running on port http://localhost:${PORT}/graphiql`);
});

