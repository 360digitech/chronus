import Vue from 'vue'
import Router from 'vue-router'
import SystemList from './SystemList'
Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/system',
      name: 'system',
      component: SystemList,
      meta: {
        menuShow: true,
        icon: 'el-icon-news',
        title: '系统管理'
      }
    }
  ]
})
