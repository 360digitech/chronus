import Vue from 'vue'
import App from './app.vue'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import router from './routes.js'
import http from './Http.js'
import i18n from './lang'
import './icons' // icon
import store from './store'
Vue.prototype.$http = http
Vue.use(ElementUI, {
  i18n: (key, value) => i18n.t(key, value)
})
Vue.use(ElementUI)

router.beforeEach((to, from, next) => {
  // 判断该路由是否需要登录权限

  // 通过vuex state获取当前的token是否存在
  if (store.getters.isLoggedIn) {
    if (to.path === '/login') {
      next('/')
    } else {
      next()
    }
  } else if (to.path === '/login') {
    next()
  } else {
    next('/login')
  }
})

new Vue({
  router,
  i18n,
  store,
  render: h => h(App)
}).$mount('#app')
