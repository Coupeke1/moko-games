import validIdCheck from "@/lib/id.ts";
import axios from "axios";
import {type GameState} from "../models/game-state";
import {useAuthStore} from "@/stores/auth-store";
import {environment} from "@/config.ts";

const BASE_URL = environment.checkersService;

const axiosInstance = axios.create({
    baseURL: BASE_URL,
});

axiosInstance.interceptors.request.use(
    async (config) => {
        const token = await useAuthStore.getState().getValidToken();
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

export async function getGameState(gameId: string): Promise<GameState> {
    try {
        validIdCheck(gameId);

        const {data} = await axiosInstance.get<GameState>(`/${gameId}`);
        return data;
    } catch (error) {
        if (error instanceof Error) {
            throw new Error(`Could not fetch game with id '${gameId}': ${error.message}`);
        }
        throw new Error(`Could not fetch game with id '${gameId}': Unknown error`);
    }
}

export async function requestMove(gameId: string, playerId: string, cells: number[]): Promise<GameState> {
    try {
        const moveRequest = {
            playerId: playerId,
            cells: cells,
        };

        const response = await axiosInstance.post<GameState>(`/${gameId}/move`, moveRequest);
        return response.data;
    } catch (error) {
        if (axios.isAxiosError(error)) {
            throw new Error(`Could not make move: ${error.response?.data?.message || error.message}`);
        }
        throw new Error(`Could not make move: Unknown error`);
    }
}

export async function requestBotMove(gameId: string): Promise<GameState> {
    try {
        const response = await axiosInstance.post<GameState>(`/${gameId}/move-bot`);
        return response.data;
    } catch (error) {
        if (axios.isAxiosError(error)) {
            throw new Error(`Could not request bot move: ${error.response?.data?.message || error.message}`);
        }
        throw new Error(`Could not request bot move: Unknown error`);
    }
}