import axios from 'axios';
import { env } from './env';

let tokenGetter: (() => Promise<string | null>) | null = null;

export function setTokenGetter(getter: () => Promise<string | null>) {
    tokenGetter = getter
}

export const api = axios.create({
    baseURL: env.BACKEND_URL,
    headers: {
        "Content-Type": "application/json"
    },
    withCredentials: true
})

api.interceptors.request.use(async (config) => {
    if (!tokenGetter) return config;

    const token = await tokenGetter();

    if (token) {
        config.headers = config.headers || {};
        config.headers.Authorization = `Bearer ${token}`
    }

    return config;
})