import Vue from 'vue'
import Router from 'vue-router'
import Cluster from './Cluster'
Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/cluster',
      name: 'cluster',
      component: Cluster,
      meta: {
        menuShow: true,
        icon: 'el-icon-cloudy',
        title: '集群管理'
      }
    }]
})
