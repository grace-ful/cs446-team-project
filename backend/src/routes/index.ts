import { Router } from "express";
import userRouter from "./user";
import exerciseRouter from "./exercise";
import workoutRouter from "./workout";
import precordRouter from "./precord";
import matchRouter from "./match";

const router = Router();

router.use('/user', userRouter);
router.use('/exercises', exerciseRouter);
router.use('/workout', workoutRouter);
router.use('/pr', precordRouter);
router.use('/match', matchRouter);

export default router;