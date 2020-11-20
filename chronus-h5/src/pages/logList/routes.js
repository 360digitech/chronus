import Vue from 'vue'
import Router from 'vue-router'
import LogList from './LogList'
Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/log',
      name: 'log',
      component: LogList,
      meta: {
        menuShow: true,
        icon: 'el-icon-toilet-paper',
        title: '日志查询'
      }
    }
  ]
})
