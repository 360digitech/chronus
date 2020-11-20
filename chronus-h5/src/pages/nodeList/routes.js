import Vue from 'vue'
import Router from 'vue-router'
import NodeList from './NodeList'
Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/node',
      name: 'node',
      component: NodeList,
      meta: {
        menuShow: true,
        icon: 'el-icon-aim',
        title: '节点管理'
      }
    }
  ]
})
