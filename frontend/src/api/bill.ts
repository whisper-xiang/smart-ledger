import request from "./request";

export interface Bill {
  id: number;
  userId: number;
  type: "income" | "expense";
  category: string;
  amount: number;
  description?: string;
  billDate: string;
  createTime: string;
}

export interface BillParams {
  id?: number;
  type: string;
  category: string;
  amount: number;
  description?: string;
  billDate: string;
}

export interface BillQueryParams {
  type?: string;
  category?: string;
  startDate?: string;
  endDate?: string;
  page?: number;
  size?: number;
}

export interface PageResult<T> {
  total: number;
  records: T[];
}

export interface MonthStatistics {
  income: number;
  expense: number;
  balance: number;
}

export interface CategoryStatistics {
  category: string;
  amount: number;
}

export interface DailyTrend {
  date: string;
  type: string;
  amount: number;
}

export const billApi = {
  getBillList(params: BillQueryParams): Promise<PageResult<Bill>> {
    return request.get("/bills", { params });
  },

  getBillById(id: number): Promise<Bill> {
    return request.get(`/bills/${id}`);
  },

  addBill(data: BillParams): Promise<void> {
    return request.post("/bills", data);
  },

  updateBill(id: number, data: BillParams): Promise<void> {
    return request.put(`/bills/${id}`, data);
  },

  deleteBill(id: number): Promise<void> {
    return request.delete(`/bills/${id}`);
  },

  getRecentBills(limit: number = 5): Promise<Bill[]> {
    return request.get("/bills/recent", { params: { limit } });
  },

  getMonthStatistics(year: number, month: number): Promise<MonthStatistics> {
    return request.get("/bills/statistics/month", { params: { year, month } });
  },

  getCategoryStatistics(
    type: string,
    startDate: string,
    endDate: string
  ): Promise<CategoryStatistics[]> {
    return request.get("/bills/statistics/category", {
      params: { type, startDate, endDate },
    });
  },

  getDailyTrend(startDate: string, endDate: string): Promise<DailyTrend[]> {
    return request.get("/bills/statistics/trend", {
      params: { startDate, endDate },
    });
  },
};
