import Vue from 'vue'
import Router from 'vue-router'
import Authorization from './Authorization'
Vue.use(Router)

export default new Router({
  routes: [

    {
      path: '/',
      name: 'authorization',
      component: Authorization,
      meta: {
        menuShow: true,
        icon: 'el-icon-setting',
        title: '用户管理'
      }
    }
  ]
})
