import Vue from 'vue'
import Router from 'vue-router'

import TagList from './TagList'
Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/tag',
      name: 'tag',
      component: TagList,
      meta: {
        menuShow: true,
        icon: 'el-icon-collection-tag',
        title: 'Tag管理'
      }
    }
  ]
})
