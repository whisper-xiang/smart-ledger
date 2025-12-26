import request from "./request";

export interface LoginParams {
  username: string;
  password: string;
}

export interface RegisterParams {
  username: string;
  password: string;
  email: string;
}

export interface LoginResult {
  token: string;
  userId: number;
  username: string;
  email: string;
}

export const authApi = {
  login(data: LoginParams): Promise<LoginResult> {
    return request.post("/auth/login", data);
  },

  register(data: RegisterParams): Promise<void> {
    return request.post("/auth/register", data);
  },
};
