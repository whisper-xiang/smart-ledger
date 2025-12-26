import { createRouter, createWebHistory } from "vue-router";
import type { RouteRecordRaw } from "vue-router";

const routes: RouteRecordRaw[] = [
  {
    path: "/login",
    name: "Login",
    component: () => import("@/views/Login.vue"),
    meta: { title: "登录" },
  },
  {
    path: "/register",
    name: "Register",
    component: () => import("@/views/Register.vue"),
    meta: { title: "注册" },
  },
  {
    path: "/",
    name: "Layout",
    component: () => import("@/layout/Layout.vue"),
    redirect: "/dashboard",
    children: [
      {
        path: "dashboard",
        name: "Dashboard",
        component: () => import("@/views/Dashboard.vue"),
        meta: { title: "首页概览" },
      },
      {
        path: "bills",
        name: "Bills",
        component: () => import("@/views/Bills.vue"),
        meta: { title: "账单管理" },
      },
      {
        path: "diary",
        name: "Diary",
        component: () => import("@/views/Diary.vue"),
        meta: { title: "记账日记" },
      },
      {
        path: "savings",
        name: "Savings",
        component: () => import("@/views/Savings.vue"),
        meta: { title: "存钱计划" },
      },
      {
        path: "statistics",
        name: "Statistics",
        component: () => import("@/views/Statistics.vue"),
        meta: { title: "统计分析" },
      },
      {
        path: "profile",
        name: "Profile",
        component: () => import("@/views/Profile.vue"),
        meta: { title: "个人中心" },
      },
    ],
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

router.beforeEach((to, _from, next) => {
  document.title = `${to.meta.title || "智能记账"} - Smart Ledger`;
  const token = localStorage.getItem("token");
  if (!token && to.path !== "/login" && to.path !== "/register") {
    next("/login");
  } else {
    next();
  }
});

export default router;
