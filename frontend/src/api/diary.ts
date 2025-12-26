import request from "./request";
import type { PageResult } from "./bill";

export interface Diary {
  id: number;
  userId: number;
  title: string;
  content: string;
  mood?: string;
  diaryDate: string;
  createTime: string;
}

export interface DiaryParams {
  id?: number;
  title: string;
  content: string;
  mood?: string;
  diaryDate: string;
}

export interface DiaryQueryParams {
  startDate?: string;
  endDate?: string;
  page?: number;
  size?: number;
}

export const diaryApi = {
  getDiaryList(params: DiaryQueryParams): Promise<PageResult<Diary>> {
    return request.get("/diaries", { params });
  },

  getDiaryById(id: number): Promise<Diary> {
    return request.get(`/diaries/${id}`);
  },

  addDiary(data: DiaryParams): Promise<void> {
    return request.post("/diaries", data);
  },

  updateDiary(id: number, data: DiaryParams): Promise<void> {
    return request.put(`/diaries/${id}`, data);
  },

  deleteDiary(id: number): Promise<void> {
    return request.delete(`/diaries/${id}`);
  },
};
