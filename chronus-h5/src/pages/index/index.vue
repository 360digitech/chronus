<template>
  <el-container class="main">
    <el-aside width="">
      <el-menu
        :collapse="isCollapse"
        class="menu"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
        :default-active="jupyterUrl"
      >
        <el-row type="flex" align="middle" justify="center" style="height:60px">
          <transition name="el-fade-in-linear">
            <img src='../../../static/img/logo_white.png' style="width:120px" v-show='!isCollapse'/>
          </transition>
          <transition name="el-fade-in-linear">
              <img src='../../../static/img/favicon.png' style="width:50px" v-show='isCollapse'/>
          </transition>
        </el-row>
            <!-- $router.options.routes[0]["children"]' -->
            <!-- <template v-for='(item,index) in $router.options.routes[0]["children"] '> -->
            <template v-for='(item,index) in menu_list '>
                <el-menu-item
                  :key="index"
                  :index="item.path"
                  @click="clickMenu(item.path)"
                >
                  <i :class="item.meta.icon"></i>
                  <span slot="title">{{item.meta.title}}</span>
                </el-menu-item>
            </template>
        <!--<template v-for='(unit_menu,index) in menus'>
          <el-menu-item v-if="menus.indexOf('task') !== -1" :key="index"  @click="clickMenu(unit_menu.path)" :index="unit_menu.path">
            <i :class="unit_menu.meta.icon"></i>
            <span slot="title">{{unit_menu.meta.title}}</span>
          </el-menu-item>
        </template>-->
      </el-menu>
    </el-aside>
    <el-container>
      <el-header>
        <el-card shadow="never">
          <el-row type="flex" align="middle" style="height:60px" justify="space-between">
              <div>
                <el-row type="flex" align="middle">
                  <div style="margin-left:10px">
                    <i class="el-icon-s-unfold" v-if='isCollapse' @click="collapse"></i>
                    <i class="el-icon-s-fold" v-else @click="collapse"></i>
                  </div>
                  <!--<el-breadcrumb separator-class="el-icon-arrow-right" style="margin-left:15px">
                    <template v-for="(item, index)  in levelList">
                      <el-breadcrumb-item :key="index" v-if='item.meta.title'>
                        {{item.meta.title}}
                      </el-breadcrumb-item>
                    </template>
                  </el-breadcrumb>-->
                  </el-row>
              </div>
              <div>
                <el-dropdown style="margin-right:40px;font-size:16px;margin-left:20px" @command="logout">
                  <span class="el-dropdown-link">
                    {{$store.state.userName}}<i class="el-icon-arrow-down el-icon--right"></i>
                  </span>
                  <el-dropdown-menu slot="dropdown">
                    <el-dropdown-item>
                        退出
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </el-dropdown>
              </div>
            </el-row>
          </el-card>
      </el-header>
      <el-container>
        <el-main style="overflow: visible;padding: 0px 5px;">
          <el-row style="margin-top: 20px;" v-loading="loading">
            <iframe
              id='iframeId'
              :src="jupyterUrl"
              scrolling="no"
              frameborder="0"
              style="height:calc(100vh - 105px); width:100%;margin-top: 20px;"
              @load="iframeOnload()"
            >
            </iframe>
          </el-row>
        </el-main>
      </el-container>
    </el-container>
  </el-container>
</template>

<script>
export default {
  name: 'HelloWorld',
  data () {
    return {
      loading: false,
      isShowIframe: true,
      // 默认加载至任务列表
      jupyterUrl: '/taskList.html',
      breadlist: '',
      isCollapse: false,
      lang: 'zh',
      levelList: [],
      menu_list: [
        {
          path: '/taskList.html',
          name: 'task',
          meta: {
            menuShow: true,
            icon: 'el-icon-menu',
            title: '任务管理'
          }
        },
        {
          path: '/groupList.html',
          name: 'group',
          meta: {
            menuShow: true,
            icon: 'el-icon-cpu',
            title: '分组管理'
          }
        },
        {
          path: '/systemList.html',
          name: 'system',
          meta: {
            menuShow: true,
            icon: 'el-icon-cpu',
            title: '系统管理'
          }
        },
        {
          path: '/tagList.html',
          name: 'tag',
          meta: {
            menuShow: true,
            icon: 'el-icon-collection-tag',
            title: 'Tag管理'
          }
        },
        {
          path: '/nodeList.html',
          name: 'node',
          meta: {
            menuShow: true,
            icon: 'el-icon-aim',
            title: '节点管理'
          }
        },
        {
          path: '/cluster.html',
          name: 'cluster',
          meta: {
            menuShow: true,
            icon: 'el-icon-cloudy',
            title: '集群管理'
          }
        },
        {
          path: '/logList.html',
          name: 'log',
          meta: {
            menuShow: true,
            icon: 'el-icon-toilet-paper',
            title: '日志查询'
          }
        },

        {
          path: '/authorization.html',
          name: 'authorization',
          meta: {
            menuShow: true,
            icon: 'el-icon-setting',
            title: '授权分组'
          }
        }
      ]
    }
  },
  methods: {
    // 跳转至登录页
    skipToLogin: function () {
      const {
        protocol,
        host
      } = window.location
      window.location.href = `${protocol}//${host}/login`
    },
    // 折叠导航栏
    collapse: function () {
      this.isCollapse = !this.isCollapse
    },
    logout () {
      this.$http.post('/api/logout').then(() => {
        this.$store.dispatch('LogOut').then(() => {
          // this.$router.push('/login')
          this.skipToLogin()
        })
      })
    },
    getMenu () {
      this.$http.get('/api/menu').then(res => {
        if (res instanceof Array) {
          this.menu_list = this.menu_list.filter(item => res.includes(item.name))
        }
      })
    },
    clickMenu (url) {
      this.jupyterUrl = url
    },
    iframeOnload () {
      if (this.jupyterUrl != null) {
        this.loading = false
      }
    }

  },
  mounted: function () {
    // 先检查登录态 如果非登录态 要跳转到登录页面
    // if (!this.$store.getters.isLoggedIn) {
    //   this.skipToLogin()
    // }
    // 获取菜单列表
    this.getMenu()
  }
}
</script>
<style lang='scss'>
    .main {
      height: 100%;

      .el-header {
          padding: 0px;
          font-size: 25px;
      }

      .el-card__body {
          padding: 0px;
      }

      .menu {
          height: 100%;
      }

      .menu:not(.el-menu--collapse) {
          width: 150px;
      }

      .el-menu--collapse {
          width: 66px;
      }
    }
</style>
