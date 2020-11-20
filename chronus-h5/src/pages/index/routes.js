import Vue from 'vue'
import Router from 'vue-router'

import index from './index'
Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/',
      name: 'index',
      component: index,
      meta: {
        menuShow: true,
        icon: 'el-icon-news',
        title: '系统管理'
      }
    }
  ]
})
