<template>
  <el-container class="main">
    <el-aside width="">

      <el-menu :collapse="isCollapse" router class="menu" background-color="#304156" text-color="#bfcbd9"
        active-text-color="#409EFF">
        <el-row type="flex" align="middle" justify="center" style="height:60px">
          <transition name="el-fade-in-linear">
            <img src='../../static/img/logo_white.png' style="width:120px" v-show='!isCollapse' />

          </transition>
          <transition name="el-fade-in-linear">
            <img src='../../static/img/favicon.png' style="width:50px" v-show='isCollapse' />
          </transition>
        </el-row>

        <template v-for='(item,index) in $router.options.routes[0]["children"]'>
          <el-menu-item :key="index" :index="item.path" v-if="item.meta.menuShow">
            <i :class="item.meta.icon"></i>
            <span slot="title">{{item.meta.title}}</span>
          </el-menu-item>

        </template>

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
            <el-breadcrumb separator-class="el-icon-arrow-right" style="margin-left:15px">
              <template v-for="(item, index)  in levelList">
                <el-breadcrumb-item :key="index" v-if='item.meta.title'>
                  {{item.meta.title}}
                </el-breadcrumb-item>
              </template>
            </el-breadcrumb>
              </el-row>
            </div>
            <div>
              <el-select v-model="cluster" placeholder="请选择">
                <el-option v-for="item in clusterList" :key="item.cluster" :label="item.cluster" :value="item.cluster">
                </el-option>
              </el-select>
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
      <el-main style="overflow: visible;padding: 10px;">
        <router-view></router-view>
      </el-main>
    </el-container>

  </el-container>
</template>

<script>
export default {
  name: 'HelloWorld',
  data () {
    return {
      loading: false,
      jupyterUrl: null,
      menu_list: [],
      breadlist: '',
      isCollapse: false,
      lang: 'zh',
      levelList: [],
      clusterList: [],
      cluster: ''
    }
  },
  methods: {

    // 折叠导航栏
    collapse: function () {
      this.isCollapse = !this.isCollapse
    },

    getBreadcrumb () {
      let matched = this.$route.matched.slice(1).filter(item => item.name)
      const currentMatch = matched[0]
      const parent = currentMatch && currentMatch.parent
      const { showParent } = this.$route.meta
      if (parent && showParent) {
        parent.to = parent.path.replace(/\/:.*?(\/|$)/g, '/').replace(/\/$/, '')
        if (parent.redirect) {
          parent.to += '/' + parent.redirect
        }
        matched.unshift(parent)
      }
      // matched = [{ path: '/', meta: { title: '首页' } }].concat(matched)
      this.levelList = matched
    },
    logout () {
      this.$store.dispatch('LogOut').then(() => {
        this.$http.post('/api/logout').then(() => {
        })
        this.$router.push('/login')
      })
    }

  },
  mounted: function () {
    this.clusterList = []
    this.$http.get('/api/cluster/getAllCluster').then(res => {
      this.clusterList = res
      this.cluster = res[0].cluster

      this.$store.dispatch('ClusterChange', this.cluster)
            .then(() => { console.log('clusterChange---->'+cluster) })
    })
  },
  watch: {
    $route (to, from) {
      this.getBreadcrumb()
    },
    'cluster': function (val, oldVal) {
        if (val) {
          this.$store.dispatch('ClusterChange', val).then(() => { console.log('clusterChange---->'+val) })
        }
    } 
  }
}
</script>
<style    lang='scss'>
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
