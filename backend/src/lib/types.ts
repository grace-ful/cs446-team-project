import { Request } from "express";

export enum TimePreference {
	MORNING = "MORNING",
	AFTERNOON = "AFTERNOON",
	EVENING = "EVENING",
	NIGHT = "NIGHT",
	NONE = "NONE",
}

export enum ExperienceLevel {
	BEGINNER = "BEGINNER",
	INTERMEDIATE = "INTERMEDIATE",
	ADVANCED = "ADVANCED",
	ATHLETE = "ATHLETE",
	COACH = "COACH",
}

export enum GymFrequency {
	NEVER = "NEVER",
	RARELY = "RARELY",
	OCCASIONALLY = "OCCASIONALLY",
	REGULARLY = "REGULARLY",
	FREQUENTLY = "FREQUENTLY",
	DAILY = "DAILY",
}

export enum MuscleGroup {
	CHEST = "CHEST",
	BACK = "BACK",
	SHOULDERS = "SHOULDERS",
	ARMS = "ARMS",
	LEGS = "LEGS",
	CORE = "CORE",
	FULL_BODY = "FULL_BODY",
	OTHER = "OTHER",
}

export enum BodyPart {
	ABS = "ABS",
	ARMS = "ARMS",
	BACK = "BACK",
	CALVES = "CALVES",
	CARDIO = "CARDIO",
	CHEST = "CHEST",
	LEGS = "LEGS",
	SHOULDERS = "SHOULDERS",
	OTHER = "OTHER",
}

export enum Equipment {
	BARBELLS = "BARBELLS",
  	BENCH = "BENCH",
  	DUMBBELL = "DUMBBELL",
  	GYM_MAT = "GYM_MAT",
  	INCLINE_BENCH = "INCLINE_BENCH",
  	KETTLEBELL = "KETTLEBELL",
  	PULL_UP_BAR = "PULL_UP_BAR",
  	SZ_BAR = "SZ_BAR",
  	SWISS_BALL = "SWISS_BALL",
  	NONE = "NONE",
  	OTHER = "OTHER"
}

export interface AuthRequest extends Request {
	userId?: string
}

export interface ChatMessage {
  senderId: string;
  receiverId: string;
  content: string;
}