import Vue from 'vue'
import Router from 'vue-router'
import GroupList from './GroupList'

Vue.use(Router)

export default new Router({
  routes: [
    {

      path: '/',
      name: 'group',
      component: GroupList,
      meta: {
        menuShow: true,
        icon: 'el-icon-cpu',
        title: '分组管理'
      }
    }]})
