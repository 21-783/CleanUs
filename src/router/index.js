import { createRouter, createWebHistory } from 'vue-router'
import HeaderView from '../components/HeaderView.vue'
import LoginView from '../components/LoginView.vue'
import SignupView from '../components/SignupView.vue'
import ForgetPW from '../components/ForgetPW'
import GotoPW from '../components/GotoPW'
import MainView from '../components/MainView.vue'
import AdminPage from '../components/AdminPage.vue'
import AccountSetting from '../components/AccountSetting.vue'
import LedgerSetting from '../components/LedgerSetting.vue'
import UserInfo from '../components/UserInfo.vue'
import TransactionDetail from '../components/TransactionTable.vue'
import PopupView from '../components/PopUpView.vue'

const routes = [
  {
    path: '/',
    name: 'MainView',
    component: MainView
  },
  {
    path: '/AccountSetting',
    name: 'AccountSetting',
    component: AccountSetting
  },
  {
    path: '/PopupView',
    name: 'PopupView',
    component: PopupView
  },
   {
    path: '/Post/:id',
    name: 'TransactionDetail',
    component: TransactionDetail,
    props: true
  },
  {
    path: '/LedgerSetting',
    name: 'LedgerSetting',
    component: LedgerSetting
  },
  {
    path: '/UserInfo',
    name: 'UserInfo',
    component: UserInfo
  },
  {
    path: '/HeaderView',
    name: 'HeaderView',
    component: HeaderView
  },
   {
    path: '/AdminPage',
    name: 'AdminPage',
    component: AdminPage
  },
  {
    path: '/LoginView',
    name: 'LoginView',
    component: LoginView
  },
  {
    path: '/SignupView',
    name: 'SignupView',
    component: SignupView
  },
   {
    path: '/ForgetPW',
    name: 'ForgetPW',
    component: ForgetPW
  },
   {
    path: '/GotoPW',
    name: 'GotoPW',
    component: GotoPW
  },
  {
    path: '/about',
    name: 'about',
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: () => import(/* webpackChunkName: "about" */ '../views/AboutView.vue')
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

export default router
