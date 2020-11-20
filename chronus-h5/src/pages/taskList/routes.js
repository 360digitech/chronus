import Vue from 'vue'
import Router from 'vue-router'
import TaskList from './TaskList'
Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/',
      name: 'task',
      component: TaskList,
      meta: {
        menuShow: true,
        icon: 'el-icon-menu',
        title: '任务管理'
      }
    }
  ]
})
