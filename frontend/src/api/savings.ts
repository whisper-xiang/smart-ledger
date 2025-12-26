import request from "./request";

export interface SavingPlan {
  id: number;
  userId: number;
  name: string;
  goalAmount: number;
  savedAmount: number;
  startDate: string;
  endDate: string;
  color: string;
  remark?: string;
  createTime: string;
}

export interface SavingPlanParams {
  id?: number;
  name: string;
  goalAmount: number;
  startDate: string;
  endDate: string;
  color?: string;
  remark?: string;
}

export const savingsApi = {
  getPlanList(): Promise<SavingPlan[]> {
    return request.get("/savings");
  },

  getPlanById(id: number): Promise<SavingPlan> {
    return request.get(`/savings/${id}`);
  },

  addPlan(data: SavingPlanParams): Promise<void> {
    return request.post("/savings", data);
  },

  updatePlan(id: number, data: SavingPlanParams): Promise<void> {
    return request.put(`/savings/${id}`, data);
  },

  deletePlan(id: number): Promise<void> {
    return request.delete(`/savings/${id}`);
  },

  deposit(id: number, amount: number): Promise<void> {
    return request.post(`/savings/${id}/deposit`, { amount });
  },
};
