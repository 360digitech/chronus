<template>
  <div style="padding:20px">
    <el-table ref="singleTable" :data="dataList" highlight-current-row border style="width: 100%"
              v-loading="tableLoading" size="mini" :header-cell-style="{background:'#F3F4F7',color:'#555'}" row-key='id'>
      <el-table-column property="tag" label="TAG" align="center" width="150"></el-table-column>
      <el-table-column property="hostName" label="HOST"  align='center'></el-table-column>
      <el-table-column property="address" label="IP地址"  align='center' width="200"></el-table-column>
      <el-table-column property="cluster" label="所属集群" align="center" width="150"></el-table-column>
      <el-table-column property="version" label="版本" align="center" width="150"></el-table-column>
      <el-table-column label="类型" align="center">
        <template slot-scope="scope">
          <el-tag v-if='scope.row.isMaster==="Y"'>MASTER </el-tag>
          <el-tag v-if='scope.row.isMaster==="N" && scope.row.enableMaster==="Y"' type="info">MASTER </el-tag>
          <el-tag v-if='scope.row.enableWorker==="Y"'> WORKER</el-tag>
        </template>
      </el-table-column>
      <el-table-column property="state" label="状态" align="center" width="100">
        <template slot-scope="scope">
          <el-tag v-if='scope.row.state==="INIT"'>初始化</el-tag>
          <el-tag v-if='scope.row.state==="NORMAL"'>正常</el-tag>
          <el-tag v-if='scope.row.state==="OFFLINE"'>下线</el-tag>
          <el-tag v-if='scope.row.state==="DEAD"'>死亡</el-tag>
        </template>
      </el-table-column>
      <el-table-column property="operation" fixed="right" label="操作" align="center" width="250">
        <template slot="header" slot-scope="scope">
          <el-button size="mini" type="success" @click="onQuery">查询</el-button>
        </template>
        <template slot-scope="scope">
          <el-button v-if='scope.row.state!=="DEAD"&&scope.row.state==="OFFLINE"' size="mini" type="primary" @click="online(scope.row)"> 上线</el-button>
          <el-button v-if='scope.row.state!=="DEAD"&&scope.row.state==="NORMAL"' size="mini" type="danger" @click="offline(scope.row)">下线</el-button>
          <el-button size="mini" type="success" @click="showEvent(scope.row)">查看事件</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog title="事件查看" :visible.sync="eventDialogVisible"  width="1280px">
      <el-table :data="eventList" highlight-current-row border style="width: 100%"
                v-loading="eventTableLoading" size="mini" :header-cell-style="{background:'#F3F4F7',color:'#555'}" row-key='id'>
        <el-table-column type="expand" >
            <template slot-scope="scope" >
              <el-form label-position="left" inline v-if="scope.row.code == 'MASTER_TASK_ASSIGN'">
                <el-form-item>
                  <el-table  v-bind:data="JSON.parse(scope.row.content)"  highlight-current-row border  v-loading="eventTableLoading" size="mini">
                  <el-table-column prop="taskItemId" label="任务key" align="center" min-width="230"></el-table-column>
                  <el-table-column prop="state" label="分配结果" align="center" width="100"></el-table-column>
                  <el-table-column prop="oldExecAddress" label="旧执行机" align="center" min-width="180"></el-table-column>
                  <el-table-column prop="newExecAddress" label="新执行机" align="center" min-width="180"></el-table-column>
                  </el-table>
                </el-form-item>
              </el-form>
              <el-form label-position="left" inline v-if="scope.row.code == 'WORKER_LOAD_TASK'">
                <el-form-item>
                  <!-- <span>{{ scope.row.content }}</span> -->
                  <el-table :data="JSON.parse(scope.row.content)"  v-loading="eventTableLoading" size="mini" highlight-current-row border>
                  <el-table-column prop="taskItemId" label="任务key" align="center" min-width="230"></el-table-column>
                  <el-table-column prop="state" label="加载状态" align="center" width="180"></el-table-column>
                  <el-table-column prop="result" label="加载结果" align="center" width="90"></el-table-column>
                  <el-table-column prop="costTime" label="加载耗时(毫秒)" align="center" width="150"></el-table-column>
                  <el-table-column prop="message" label="其他信息" align="center" min-width="180"></el-table-column>
                  </el-table>
                </el-form-item>
              </el-form>
            </template>
        </el-table-column>
        <el-table-column property="address" label="IP地址" align="center" width="180"></el-table-column>
        <el-table-column property="version" label="版本"  align='center' width="120"> </el-table-column>
        <el-table-column property="desc" label="事件"  align='center' width="220"></el-table-column>
        <el-table-column property="message" label="消息" align="center"></el-table-column>
        <el-table-column property="costTime" label="耗时" align="center"  width="80"></el-table-column>
        <el-table-column property="dateCreated" label="发生时间" align="center" width="150"></el-table-column>
      </el-table>

    </el-dialog>
  </div>
</template>

<script>
import moment from 'moment'
export default {
  data () {
    return {
      tableLoading: false,
      eventDialogVisible: false,
      eventTableLoading: false,
      queryObj: {
        cluster: 'default'
      },
      currentObject: {
      },
      dataList: [],
      eventList: []
    }
  },
  mounted () {
    this.onQuery()

    // 监听stg的变化
    window.addEventListener('message', e => {
      if (typeof e.data === 'string') this.onQuery()
    }, false)
  },
  methods: {
    dateFormat: function (row, column) {
      var date = row[column.property]
      if (date === undefined) {
        return ''
      }
      return moment(date).format('YYYY-MM-DD HH:mm:ss')
    },
    dateFormatVal: function (date) {
      if (date === undefined) {
        return ''
      }
      return moment(date).format('YYYY-MM-DD HH:mm:ss')
    },

    onQuery () {
      this.tableLoading = true
      this.dataList = []
      this.$http.post('/api/node/getAllNode', this.queryObj).then(res => {
        this.tableLoading = false
        this.dataList = res
      })
    },
    online (row) {
      this.$http.put('/api/node/online', row).then(res => {
        this.dialogVisible = false
        this.$message.success('操作成功！')
        this.onQuery()
      }).catch(errors => {
        this.$message.error(errors.msg)
      })
    },

    offline (row) {
      this.$http.put('/api/node/offline', row).then(res => {
        this.dialogVisible = false
        this.$message.success('操作成功！')
        this.onQuery()
      }).catch(errors => {
        this.$message.error(errors.msg)
      })
    },
    showEvent (row) {
      this.eventTableLoading = true
      this.eventList = []
      this.$http.get('/api/node/log/' + row.cluster + '/' + row.address + '/' + row.version).then(res => {
        this.eventTableLoading = false
        this.eventList = res
      })
      this.eventDialogVisible = true
    }
  }
}
</script>

<style scoped>
  .tableHeader {
    background-color: red !important;
  }
</style>
