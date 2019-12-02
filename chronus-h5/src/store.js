
import Vuex from 'vuex'
import axios from 'axios'
import Vue from 'vue'
Vue.use(Vuex)

const store = new Vuex.Store({
  state: {
    status: '',
    cluster: localStorage.getItem('cluster') || 'default',
    token: localStorage.getItem('token') || '',
    userName: localStorage.getItem('userName') || ''
  },
  mutations: {
    cluster_change (state, cluster) {
      state.cluster = cluster
    },
    auth_request (state) {
      state.status = 'loading'
    },
    auth_success (state, token) {
      state.status = 'success'
      state.token = token
    },
    auth_user (state, userName) {
      state.status = 'success'
      state.userName = userName
    },
    auth_error (state) {
      state.status = 'error'
    },
    logout (state) {
      state.status = ''
      state.userName = ''
      state.token = ''
    }
  },
  actions: {
    Login ({commit}, user) {
      return new Promise((resolve, reject) => {
        commit('auth_request')
        // 向后端发送请求，验证用户名密码是否正确，请求成功接收后端返回的token值，利用commit修改store的state属性，并将token存放在localStorage中
        axios.post('/api/login', user)
          .then(resp => {
            if (resp.data.flag === 'S') {
              resolve(resp.data.data)
            } else {
              reject(resp.data.msg)
            }
            localStorage.setItem('token', resp.data.data)
            localStorage.setItem('userName', user.name)
            // 每次请求接口时，需要在headers添加对应的Token验证
            // 更新token
            commit('auth_success', resp.data.data)
            commit('auth_user', user.name)
          })
          .catch(err => {
            commit('auth_error')
            localStorage.removeItem('token')
            localStorage.removeItem('userName')
            reject(err)
          })
      })
    },
    LogOut ({ commit }) {
      return new Promise(resolve => {
        localStorage.removeItem('token')
        localStorage.removeItem('userName')
        commit('logout')
        resolve()
      })
    },
    ClusterChange ({ commit }, cluster) {
      return new Promise(resolve => {
        localStorage.removeItem('cluster')
        commit('cluster_change', cluster)
      })
    }
  },
  getters: {
    // !!将state.token强制转换为布尔值，若state.token存在且不为空(已登录)则返回true，反之返回false
    isLoggedIn: state => !!state.token,
    authStatus: state => state.status,
    getToken: state => state.token
  }
})

export default store
