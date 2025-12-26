import request from "./request";

export interface UserInfo {
  id: number;
  username: string;
  email: string;
  phone?: string;
  avatar?: string;
  currency: string;
  monthlyBudget: number;
  createTime: string;
}

export interface UpdatePasswordParams {
  oldPassword: string;
  newPassword: string;
}

export const userApi = {
  getUserInfo(): Promise<UserInfo> {
    return request.get("/user/info");
  },

  updateUserInfo(data: Partial<UserInfo>): Promise<void> {
    return request.put("/user/info", data);
  },

  updatePassword(data: UpdatePasswordParams): Promise<void> {
    return request.put("/user/password", data);
  },
};
