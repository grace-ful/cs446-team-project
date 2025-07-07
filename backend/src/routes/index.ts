import { Router } from "express";
import userRouter from "./user";
import workoutTemplateRouter from "./workoutTemplate";
import exerciseTemplateRouter from "./exerciseTemplate";
import exerciseSessionRouter from "./exerciseSession";
import exerciseSetRouter from "./exerciseSet";
import workoutSessionRouter from "./workoutSession";
import prRouter from "./pr";

const router = Router();

router.use("/user", userRouter);
router.use("/workout-template", workoutTemplateRouter);
router.use("/exercise-template", exerciseTemplateRouter);
router.use("/exercise-sessions", exerciseSessionRouter);
router.use("/exercise-set", exerciseSetRouter);
router.use("/workout-sessions", workoutSessionRouter);
router.use("/pr", prRouter);

export default router;
