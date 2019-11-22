<template>
  <div style="padding:20px">

    <el-form :inline="true" @submit.native.prevent>
      <el-form-item label="系统">
        <el-select v-model="queryObj.dealSysCode" placeholder="请选择" clearable filterable>
          <el-option v-for="item in allSysCodes" :key="item" :label="item" :value="item">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="任务名称">
        <el-input size="medium" v-model="queryObj.taskName" placeholder="请输入内容" clearable style="width:200px"
          @keyup.enter.native="onQuery"></el-input>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="queryObj.state" placeholder="请选择" clearable>
          <el-option label="启动" value="normal"></el-option>
          <el-option label="停止" value="pause"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button size="medium" type="primary" @click="onQuery">查询</el-button>
        <el-button size="medium" type="success" @click="newObject">新建</el-button>
      </el-form-item>
    </el-form>

    <el-table ref="singleTable" :data="dataList" highlight-current-row border style="width: 100%"
      v-loading="tableLoading" size="mini" @row-dblclick="onShow" :header-cell-style="{background:'#F3F4F7',color:'#555'}"
      row-key='id'>
      <el-table-column property="tag" label="tag" align="center" width="110"></el-table-column>
      <el-table-column property="taskName" label="任务名称" min-width="140" align='center'> </el-table-column>
      <el-table-column property="remark" label="备注" align="center" min-width="140"> </el-table-column>
      <el-table-column property="assignNum" label="线程组数量" align="center" width="90"> </el-table-column>
      <el-table-column property="threadNumber" label="线程数" align="center" width="60"> </el-table-column>
      <el-table-column property="permitRunStartTime" label="启动时间" align="center" width="150"> </el-table-column>
      <el-table-column property="state" label="状态" align="center" width="90">
        <template slot-scope="scope">
            <el-tag v-if='scope.row.state==="normal"' type="success" size="mini" @click.stop="runtimeInfo(scope.row)">正常 </el-tag>
            <el-tag v-else  size="mini"> 暂停</el-tag>
            <i v-if="taskChangeStateMap[scope.row.id]" class="el-icon-loading"></i>
            <el-tag size="mini" v-if="taskChangeResultMap[scope.row.id+'succCount'] > 0" type="success">{{taskChangeResultMap[scope.row.id+'succCount']}}</el-tag>
            <el-tag size="mini" v-if="taskChangeResultMap[scope.row.id+'failCount'] > 0" type="danger">{{taskChangeResultMap[scope.row.id+'failCount']}}</el-tag>
         </template>
      </el-table-column>
      <el-table-column property="dateUpdated" label="更新时间" align="center" width="150" :formatter="dateFormat">      </el-table-column>
      <el-table-column property="updatedBy" label="更新人" align="center" width="80"> </el-table-column>

      <el-table-column property="operation" fixed="right" label="操作" align="center" width="300">
        <template slot-scope="scope">
          <el-button v-if="scope.row.state==='pause'" plain size="mini" type="info" @click.stop="changeStatus(scope.row,'normal')">开始</el-button>
          <el-button v-if="scope.row.state==='normal'" plain size="mini" type="warning" @click.stop="changeStatus(scope.row,'pause')">暂停</el-button>
          <el-button size="mini" type="primary" @click.stop="editObject(scope.row)">编辑</el-button>
          <el-button size="mini" type="warning" @click.stop="copyObject(scope.row)">复制</el-button>
          <el-button size="mini" type="danger" @click.stop="deleteObject(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-row type="flex" justify="center" style="margin-top:15px;">
      <el-pagination background layout="total, prev, pager, next, jumper" :current-page.sync="queryObj.pageNum"
        @current-change="onQuery" :total="total" :page-size="queryObj.pageSize">
      </el-pagination>
    </el-row>

    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="1280px">
      <el-form label-position='right' label-width="170px" size="mini" :model="detailObject" ref='objForm' >
        <el-row>
          <el-col :span="8">
            <el-form-item label="集群" prop="cluster" :rules="rule">
              <el-select v-model="detailObject.cluster" placeholder="请选择" :disabled="this.EditType=='edit'" >
                <el-option v-for="item in clusterList" :key="item.cluster" :label="item.cluster" :value="item.cluster">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="系统编码" prop="dealSysCode" :rules="rule">
              <el-select v-model="detailObject.dealSysCode" placeholder="请选择">
                <el-option v-for="item in allSysCodes" :key="item" :label="item" :value="item">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="tag" prop="tag" :rules="rule">
              <el-select v-model="detailObject.tag" placeholder="请选择">
                <el-option v-for="item in allTagCodes" :key="item" :label="item" :value="item" >
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>

        </el-row>
        <el-row>
         <el-col :span="8">
            <el-form-item label="BeanName" prop="dealBeanName" :rules="rule">
              <el-select v-model="detailObject.dealBeanName" >
                <el-option label="DUBBO" value="DUBBO"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="调度类型" prop="processorType" :rules="rule">
              <el-select v-model="detailObject.processorType" placeholder="调度类型">
                <el-option label="SLEEP" value="SLEEP"></el-option>
                <el-option label="SIMPLE" value="SIMPLE"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="按表达式执行">
              <el-select v-model="detailObject.forceCronExec">
                <el-option label="Y(强制按表达式配置执行)" value="Y"></el-option>
                <el-option label="N(有数据继续调度)" value="N"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
           <el-col :span="12">
            <el-form-item label="任务名称" prop="taskName" :rules="rule">
              <el-input v-model="detailObject.taskName"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="业务系统Bean" prop="dealBizBeanName" :rules="rule">
              <el-input v-model="detailObject.dealBizBeanName"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="开始执行时间" prop="permitRunStartTime" :rules="rule">
              <el-input v-model="detailObject.permitRunStartTime"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束执行时间">
              <el-input v-model="detailObject.permitRunEndTime"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="没有数据休眠">
              <el-input v-model="detailObject.sleepTimeNoData"></el-input>
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="处理完数据休眠">
              <el-input v-model="detailObject.sleepTimeInterval"></el-input>
            </el-form-item>
          </el-col>
            </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="每次获取数据量">
              <el-input v-model="detailObject.fetchDataNumber"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="每次处理数据量">
              <el-input v-model="detailObject.executeNumber"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="线程组数量">
              <el-input v-model="detailObject.assignNum"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="分发处理线程数">
              <el-input v-model="detailObject.threadNumber"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="备注">
              <el-input v-model="detailObject.remark"></el-input>
            </el-form-item>
          </el-col>
           <el-col :span="12">
            <el-form-item label="任务项">
              <el-input v-model="detailObject.taskItems"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="任务参数">
          <el-input v-model="detailObject.taskParameter"></el-input>
        </el-form-item>
      </el-form>

      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="validateObj">确 定</el-button>
      </span>

    </el-dialog>

    <el-dialog :title="runtimeDialogTitle" :visible.sync="runtimeDialogVisible" width="1700px">
      <el-table ref="singleTable" :data="runtimeList" highlight-current-row border style="width: 100%" v-loading="tableLoading" size="mini" :header-cell-style="{background:'#F3F4F7',color:'#555'}" row-key='id'>
        <el-table-column type="expand">
        <template slot-scope="props">
          <el-form label-position="left" inline class="demo-table-expand">
            <el-form-item label="描述信息">
              <span>{{ props.row.message }}</span>
            </el-form-item>
          </el-form>
        </template>
      </el-table-column>
      <el-table-column property="seqNo" label="序号" align="center" width="50"> </el-table-column>
      <el-table-column property="registerTime" label="注册时间" align="center" width="150" :formatter="dateFormat"> </el-table-column>
      <el-table-column property="hostName" label="主机名称" align="center" min-width="100"> </el-table-column>
      <el-table-column property="address" label="执行机" align="center" width="150"> </el-table-column>
      <el-table-column property="heartBeatTime" label="最近心跳时间" align="center" width="150" :formatter="dateFormat"> </el-table-column>
      <el-table-column property="state" label="状态" align="center" width="70">
        <template slot-scope="scope">
            <el-tag v-if='scope.row.state==="init"'  size="mini">初始化</el-tag>
            <el-tag v-if='scope.row.state==="normal"' type="success" size="mini">运行中</el-tag>
            <el-tag v-if='scope.row.state==="pause"'  size="mini">等待中</el-tag>
            <el-tag v-if='scope.row.state==="error"'  size="mini">异常</el-tag>
            <el-tag v-if='scope.row.state==="dead"'  size="mini">死亡</el-tag>
         </template>
      </el-table-column>
      <el-table-column property="lastFetchDataTime" label="最近一次取数据时间" align="center" width="150" :formatter="dateFormat"> </el-table-column>
      <el-table-column property="nextRunStartTime" label="下次开始时间" align="center" width="150" :formatter="dateFormat"> </el-table-column>
      <el-table-column property="nextRunEndTime" label="下次结束时间" align="center" width="150" :formatter="dateFormat"> </el-table-column>
      <el-table-column property="taskItems" label="任务项" align="center"> </el-table-column>
    </el-table>
    </el-dialog>

    <el-drawer :title="'任务['+currentObject.taskName+']信息'" :visible.sync="detailShow" direction="ttb" class="expandDrawer" size='300'>
      <el-form :inline="true" style='padding:0px 30px'>
        <el-row>
           <el-col :span="6">
            <el-form-item label="业务系统处理Bean">
              {{currentObject.dealBizBeanName}}
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="调度类型">
              {{currentObject.processorType}}
            </el-form-item>
          </el-col>
           <el-col :span="6">
            <el-form-item label="系统处理Bean">
              {{currentObject.dealBeanName}}
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="心跳频率(s)">
              {{currentObject.heartBeatRate}}
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="6">
            <el-form-item label="执行开始">
              {{currentObject.permitRunStartTime}}
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="执行结束">
              {{currentObject.permitRunEndTime}}
            </el-form-item>
          </el-col>

          <el-col :span="6">
            <el-form-item label="获取数/次">
              {{currentObject.fetchDataNumber}}
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="执行数/次">
              {{currentObject.executeNumber}}
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="6">
            <el-form-item label="线程组数量">
              {{currentObject.threadNumber}}
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="分发处理线程数">
              {{currentObject.threadNumber}}
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="没有数据休眠时间(s)">
              {{currentObject.sleepTimeNoData}}
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="数据处理完休眠时间(s)">
              {{currentObject.judgeDeadInterval}}
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="6">
            <el-form-item label="任务项数组">
              {{currentObject.taskItems}}
            </el-form-item>
          </el-col>
          <el-col :span="18">
            <el-form-item label="任务参数">
              {{currentObject.taskParameter}}
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </el-drawer>

  </div>
</template>

<script>
import moment from 'moment'

export default {

  data () {
    return {
      tableLoading: false,
      runtimeTableLoading: false,
      dialogBatchShow: false,
      batchOperationFalg: false,
      detailShow: false,
      dialogTitle: '新建任务',
      runtimeDialogTitle: '运行信息',
      dialogVisible: false,
      runtimeDialogVisible: false,
      EditType: 'new',
      queryObj: {
        state: '',
        cluster: '',
        pageNum: 0,
        pageSize: 10
      },
      detailObject: {
        cluster: '',
        dealSysCode: '',
        tag: '',
        dealBeanName: '',
        processorType: '',
        forceCronExec: '',
        taskName: '',
        dealBizBeanName: '',
        permitRunStartTime: '',
        permitRunEndTime: '',
        sleepTimeNoData: '',
        sleepTimeInterval: '',
        fetchDataNumber: '',
        executeNumber: '',
        assignNum: '',
        threadNumber: '',
        remark: '',
        taskItems: '',
        taskParameter: ''
      },
      currentObject: {},
      runtimeList: [],
      dataList: [],
      total: 0,
      totalData: [],
      allSysCodes: [],
      allTagCodes: [],
      taskChangeStateMap: {},
      taskChangeResultMap: {},
      clusterList: [],
      rule: [
        { required: true, message: '不能为空！', trigger: 'blur,change' }
      ]
    }
  },
  mounted () {
    this.loadAllSysCodes()
    this.onQuery()
    this.loadAllCluster()
    this.loadAllTagCodes()
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
    loadAllSysCodes () {
      this.$http.get('/api/system/loadAllSysCodes').then(res => {
        this.allSysCodes = res
      })
    },
    loadAllTagCodes () {
      this.$http.get('/api/tag/loadAllTagCodes').then(res => {
        this.allTagCodes = res
      })
    },
    onQuery () {
      this.tableLoading = true
      this.queryObj.cluster = this.$store.state.cluster
      this.$http.post('/api/task/getAll', this.queryObj).then(res => {
        this.tableLoading = false
        this.dataList = res.list
        var tmpChangeStateMap = {}
        var tmpChangeResultMap = {}
        for (var i = 0; i < this.dataList.length; i++) {
          tmpChangeStateMap[this.dataList[i].id] = false
          tmpChangeResultMap[this.dataList[i].id + 'succCount'] = 0
          tmpChangeResultMap[this.dataList[i].id + 'failCount'] = 0
        }
        this.taskChangeStateMap = tmpChangeStateMap
        this.taskChangeResultMap = tmpChangeResultMap
        this.total = res.total
      }).catch(errors => {
        this.dataList = []
        this.$message.error(errors.msg)
        this.tableLoading = false
      })
    },
    newObject () {
      this.detailObject = {}
      this.dialogTitle = '新建任务'
      this.dialogVisible = true
      this.EditType = 'new'
    },
    editObject (row) {
      this.dialogTitle = '编辑任务'
      this.dialogVisible = true
      this.EditType = 'edit'
      this.detailObject = JSON.parse(JSON.stringify(row))
    },
    copyObject (row) {
      this.dialogTitle = '同步任务至新集群(原集群仍然有任务)'
      this.dialogVisible = true
      this.EditType = 'copy'
      this.detailObject = JSON.parse(JSON.stringify(row))
    },
    runtimeInfo (row) {
      let data = {}
      data.env = row.env
      data.cluster = row.cluster
      data.taskName = row.taskName
      this.runtimeTableLoading = true
      this.runtimeList = []
      this.$http.post('/api/task/taskRuntime', data).then(res => {
        this.runtimeList = res
        this.runtimeTableLoading = true
      }).catch(errors => {
        this.$message.error(errors.msg)
        this.runtimeTableLoading = false
      })
      this.runtimeDialogTitle = '[' + row.taskName + ']运行信息'
      this.runtimeDialogVisible = true
    },
    validateObj () {
      debugger
      this.$refs['objForm'].validate((valid) => {
        if (valid) {
          this.save()
        } else {
          this.$message({
            message: '请检查，存在不符合规范的输入！',
            type: 'error'
          })

          return false
        }
      })
    },
    save () {
      var url = ''
      if (this.EditType === 'new' || this.EditType === 'copy') {
        url = '/api/task/'
        this.$http.post(url, this.detailObject).then(res => {
          this.dialogVisible = false
          this.$message.success('操作成功！')
          this.onQuery()
        }).catch(errors => {
          this.$message.error(errors.msg)
        })
      } else {
        url = '/api/task/'
        this.$http.put(url, this.detailObject).then(res => {
          this.dialogVisible = false
          this.$message.success('操作成功！')
          this.onQuery()
        }).catch(errors => {
          this.$message.error(errors.msg)
        })
      }
    },
    deleteObject (row) {
      if (row.state === 'normal') {
        this.dialogVisible = false
        this.$message({
          message: '删除失败！先停止任务再删除！',
          type: 'error'
        })
        return
      }
      this.$http.delete('/api/task/' + row.taskName).then(res => {
        this.dialogVisible = false
        this.$message({
          message: '删除成功！',
          type: 'success'
        })
        this.onQuery()
      }).catch(errors => {
        this.$message.error(errors.msg)
      })
    },
    onShow (row) {
      this.currentObject = row
      this.detailShow = true
    },
    loadAllCluster () {
      this.$http.get('/api/cluster/getAllCluster').then(res => {
        this.clusterList = res
      })
    },
    changeStatus (row, state) {
      row.state = state
      this.$http.put('/api/task/', row).then(res => {
        this.dialogVisible = false
        this.$message.success('操作成功！')
        this.refreshRuntimeState(row, state)
      }).catch(errors => {
        this.$message.error(errors.msg)
      })
    },
    refreshRuntimeState (row, state) {
      let data = {}
      data.cluster = row.cluster
      data.taskName = row.taskName
      let assignNum = row.assignNum
      let id = row.id
      let _this = this
      _this.taskChangeStateMap[id] = true
      _this.taskChangeResultMap[id + 'succCount'] = 0
      _this.taskChangeResultMap[id + 'failCount'] = 0
      var time = setInterval(() => {
        _this.$http.post('/api/task/taskRuntime', data).then(res => {
          if ((state === 'pause' && res.length == 0) || (state === 'normal' && assignNum == res.length)) {
            let failCount = 0
            let succCount = 0
            for (var i = 0; i < res.length; i++) {
              if (res[i].state === 'error') {
                failCount = failCount + 1
              } else {
                succCount = succCount + 1
              }
            }
            _this.taskChangeResultMap[id + 'succCount'] = succCount
            _this.taskChangeResultMap[id + 'failCount'] = failCount
            _this.taskChangeStateMap[id] = false
            window.clearInterval(time)
          }
          _this.runtimeList = res
        }).catch(errors => {
          _this.taskChangeStateMap[id] = false
          window.clearInterval(time)
        })
      }, 3000)
      // 通过$once来监听定时器，在beforeDestroy钩子可以被清除。
      this.$once('hook:beforeDestroy', () => {
        clearInterval(time)
      })
    }
  },
  watch: {
    '$store.state.cluster': function (val, oldVal) {
      if (val) {
        this.onQuery()
      }
    }
  }

}
</script>

<style scoped>
.tableHeader {
  background-color: red !important;
}
.expandDrawer .el-form-item{
  margin-bottom: 0px;
  height: 40px;
  width: 100%;
}
</style>
