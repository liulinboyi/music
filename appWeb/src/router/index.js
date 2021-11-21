import {
  createRouter,
  createWebHashHistory
} from 'vue-router'
import store from "../store/index";

const Recommend = () => import('@/views/recommend' /* webpackChunkName: "recommend" */ )
const Singer = () => import('@/views/singer' /* webpackChunkName: "singer" */ )
const TopList = () => import('@/views/top-list' /* webpackChunkName: "top-list" */ )
const Search = () => import('@/views/search' /* webpackChunkName: "search" */ )
const SingerDetail = () => import('@/views/singer-detail' /* webpackChunkName: "singer-detail" */ )
const Album = () => import('@/views/album' /* webpackChunkName: "album" */ )
const TopDetail = () => import('@/views/top-detail' /* webpackChunkName: "top-detail" */ )
const UserCenter = () => import('@/views/user-center' /* webpackChunkName: "user-center" */ )

const routes = [{
    path: '/',
    redirect: '/recommend'
  },
  {
    path: '/recommend',
    component: Recommend,
    children: [{
      path: ':id',
      component: Album
    }]
  },
  {
    path: '/singer',
    component: Singer,
    children: [{
      path: ':id',
      component: SingerDetail
    }]
  },
  {
    path: '/top-list',
    component: TopList,
    children: [{
      path: ':id',
      component: TopDetail
    }]
  },
  {
    path: '/search',
    component: Search,
    children: [{
      path: ':id',
      component: SingerDetail
    }]
  },
  {
    path: '/user',
    components: {
      user: UserCenter
    }
  }
]

const router = createRouter({
  history: createWebHashHistory(process.env.BASE_URL),
  routes
})

router.beforeResolve(async to => {

  if (store.state.fullScreen) {
    store.commit('setFullScreen', false)
    return false
  }
  // if (to.meta.requiresCamera) {
  //   try {
  //     await askForCameraPermission()
  //   } catch (error) {
  //     if (error instanceof NotAllowedError) {
  //       // ... 处理错误，然后取消导航
  //       return false
  //     } else {
  //       // 意料之外的错误，取消导航并把错误传给全局处理器
  //       throw error
  //     }
  //   }
  // }
})

export default router