import { Request, Response, Router } from "express";
import userRouter from "./user";
import workoutTemplateRouter from "./workoutTemplate";
import exerciseTemplateRouter from "./exerciseTemplate";
import exerciseSessionRouter from "./exerciseSession";
import exerciseSetRouter from "./exerciseSet";
import workoutSessionRouter from "./workoutSession";
import prRouter from "./pr";
import matchesRouter from "./matches";
import { chatRouter } from "./chat";

const router = Router();

router.get("/", (req: Request, res: Response) => {
    res.status(200).json({
        msg: "Welcome to the CS446 Fit4Me Backend Server",
    });
});

router.use("/user", userRouter);
router.use("/workout-template", workoutTemplateRouter);
router.use("/exercise-template", exerciseTemplateRouter);
router.use("/exercise-sessions", exerciseSessionRouter);
router.use("/exercise-set", exerciseSetRouter);
router.use("/workout-sessions", workoutSessionRouter);
router.use("/pr", prRouter);
router.use("/matches", matchesRouter);
router.use("/chat", chatRouter)

export default router;
