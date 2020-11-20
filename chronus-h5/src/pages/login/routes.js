import Vue from 'vue'
import Router from 'vue-router'
import login from './login'
Vue.use(Router)

export default new Router({
  routes: [
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
