import Vue from 'vue'
import Router from 'vue-router'

import login from './views/login'
import index from './views/index'
import SystemList from './views/SystemList'
import LogList from './views/LogList'
import TagList from './views/TagList'
import NodeList from './views/NodeList'
import TaskList from './views/TaskList'
import Cluster from './views/Cluster'
Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/',
      name: 'index',
      component: index,
      meta: {
        menuShow: true
      },
      children: [
        {
          path: '/',
          name: 'task',
          component: TaskList,
          meta: {
            menuShow: true,
            icon: 'el-icon-menu',
            title: '任务管理'
          }
        },
        {
          path: '/system',
          name: 'system',
          component: SystemList,
          meta: {
            menuShow: true,
            icon: 'el-icon-cpu',
            title: '系统管理'
          }
        },
        {
          path: '/tag',
          name: 'tag',
          component: TagList,
          meta: {
            menuShow: true,
            icon: 'el-icon-collection-tag',
            title: 'Tag管理'
          }
        },
        {
          path: '/node',
          name: 'node',
          component: NodeList,
          meta: {
            menuShow: true,
            icon: 'el-icon-aim',
            title: '节点管理'
          }
        },
        {
          path: '/cluster',
          name: 'cluster',
          component: Cluster,
          meta: {
            menuShow: true,
            icon: 'el-icon-cloudy',
            title: '集群管理'
          }
        },
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
    },
    {
      path: '/login',
      name: 'login',
      component: login,
      meta: {
        requireAuth: false
      }
    }
  ]
})
