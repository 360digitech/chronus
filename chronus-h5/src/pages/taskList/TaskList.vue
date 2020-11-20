<template>
  <div style="padding:20px">

    <el-form :inline="true" @submit.native.prevent>
      <el-form-item label="系统">
        <el-select size="medium" v-model="queryObj.dealSysCode" placeholder="请选择" clearable filterable>
          <el-option v-for="item in allSysCodes" :key="item" :label="item" :value="item">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="任务名称">
        <el-input size="medium" v-model.trim="queryObj.taskName" placeholder="请输入内容" clearable style="width:200px" @keyup.enter.native="onQuery"></el-input>
      </el-form-item>
      <el-form-item label="启动状态">
        <el-select size="medium" v-model="queryObj.state" placeholder="请选择" clearable filterable>
          <el-option label="启动" value="START"></el-option>
          <el-option label="停止" value="STOP"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button size="medium" type="primary" @click="onQuery">查询</el-button>
        <el-button size="medium" type="success" @click="newObject">新建</el-button>
      </el-form-item>
    </el-form>

    <el-table ref="singleTable" :data="dataList" highlight-current-row border style="width: 100%" v-loading="tableLoading" size="mini" @row-dblclick="onShow" :header-cell-style="{background:'#F3F4F7',color:'#555'}" row-key='id'>
      <el-table-column property="dealSysCode" label="系统编码" align="center" width="100"></el-table-column>
      <el-table-column property="tag" label="tag" align="center" width="100"></el-table-column>
      <el-table-column property="taskName" label="任务名称" min-width="140" align='center'> </el-table-column>
      <el-table-column property="remark" label="备注" align="center" min-width="140"> </el-table-column>
      <el-table-column property="cluster" label="优先集群" align="center" width="80"> </el-table-column>
      <!-- <el-table-column property="assignNum" label="线程组数量" align="center" width="90"> </el-table-column> -->
      <!-- <el-table-column property="threadNumber" label="线程数" align="center" width="60"> </el-table-column> -->
      <el-table-column property="permitRunStartTime" label="启动时间" align="center" min-width="150"> </el-table-column>
      <el-table-column property="state" label="启动状态" align="center" width="90">
        <template slot-scope="scope">
          <el-tag v-if='scope.row.state==="START"' type="success" size="mini" @click.stop="runtimeInfo(scope.row)">已启动 </el-tag>
          <el-tag v-else size="mini"> 停止</el-tag>
          <i v-if="taskChangeStateMap[scope.row.id]" class="el-icon-loading"></i>
          <el-tag size="mini" v-if="taskChangeResultMap[scope.row.id+'succCount'] > 0" type="success">{{taskChangeResultMap[scope.row.id+'succCount']}}</el-tag>
          <el-tag size="mini" v-if="taskChangeResultMap[scope.row.id+'failCount'] > 0" type="danger">{{taskChangeResultMap[scope.row.id+'failCount']}}</el-tag>
        </template>
      </el-table-column>
      <el-table-column property="dateUpdated" label="更新时间" align="center" width="150" :formatter="dateFormat"> </el-table-column>
      <el-table-column property="updatedBy" label="更新人" align="center" min-width="80"> </el-table-column>

      <el-table-column property="operation" fixed="right" label="操作" align="center" width="300">
        <template slot-scope="scope">
          <el-button v-if="scope.row.state==='STOP'" plain size="mini" type="info" @click.stop="startTask(scope.row)">开始</el-button>
          <el-button v-if="scope.row.state==='START'" plain size="mini" type="warning" @click.stop="stopTask(scope.row)">暂停</el-button>
          <el-button size="mini" type="primary" @click.stop="editObject(scope.row)">编辑</el-button>
          <el-button size="mini" type="warning" @click.stop="copyObject(scope.row)">复制</el-button>
          <el-button size="mini" type="danger" @click.stop="deleteObject(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-row type="flex" justify="center" style="margin-top:15px;">
      <el-pagination background layout="total, prev, pager, next, jumper" :current-page.sync="queryObj.pageNum" @current-change="onQuery" :total="total" :page-size="queryObj.pageSize">
      </el-pagination>
    </el-row>

    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="1280px">
      <el-form label-position='right' label-width="170px" size="medium" :model="detailObject" ref='objForm'>
        <el-row>
          <el-col :span="8">
            <el-form-item label="tag" prop="tag" :rules="rule">
              <el-select v-model="detailObject.tag" placeholder="请选择" :disabled="this.EditType=='edit'" filterable>
                <el-option v-for="item in allTagCodes" :key="item" :label="item" :value="item">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="系统编码" prop="dealSysCode" :rules="rule">
              <el-select v-model="detailObject.dealSysCode" placeholder="请选择" :disabled="this.EditType=='edit'" filterable>
                <el-option v-for="item in allSysCodes" :key="item" :label="item" :value="item">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="优先集群" prop="cluster" :rules="rule">
              <el-select v-model="detailObject.cluster" placeholder="请选择" filterable>
                <el-option v-for="item in clusterList" :key="item.cluster" :label="item.cluster" :value="item.cluster">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="8">
            <el-form-item label="调度类型" prop="processorType" :rules="rule">
                <el-select v-model="detailObject.processorType" placeholder="调度类型" clearable filterable>
                  <el-option label="Execute(简单触发类型)" value="Execute"></el-option>
                  <el-option label="SelectExecuteFlow(持续分发调度类型)" value="SelectExecuteFlow"></el-option>
                  <el-option label="SelectExecuteSimple(一次性分发调度类型)" value="SelectExecuteSimple"></el-option>
                </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="16">
            <el-form-item label="业务系统Bean" prop="dealBizBeanName" :rules="rule">
              <el-input v-model.trim="detailObject.dealBizBeanName" maxlength="64" show-word-limit></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="任务名称" prop="taskName" :rules="rule">
              <el-input v-model.trim="detailObject.taskName" maxlength="64" show-word-limit></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="开始执行时间" prop="permitRunStartTime" :rules="rule">
              <cron-input v-model="detailObject.permitRunStartTime" @change="changeCron" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="没有数据休眠">
              <el-input v-model.number="detailObject.sleepTimeNoData" :disabled="detailObject.processorType!='SelectExecuteFlow'"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="处理完数据休眠">
              <el-input v-model.number="detailObject.sleepTimeInterval" :disabled="detailObject.processorType=='Execute'"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="每次获取数据量">
              <el-input v-model.number="detailObject.fetchDataNumber" :disabled="detailObject.processorType=='Execute'"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="每次处理数据量">
              <el-input v-model.number="detailObject.executeNumber" :disabled="detailObject.processorType =='Execute'"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="线程组数量">
              <el-input v-model.number="detailObject.assignNum"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="分发处理线程数">
              <el-input v-model.number="detailObject.threadNumber" :disabled="detailObject.processorType=='Execute'"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
         <el-row>
          <el-col :span="12">
            <el-form-item label="任务项">
              <el-input v-model.trim="detailObject.taskItems"></el-input>
            </el-form-item>
          </el-col>
           <el-col :span="12">
             <el-form-item label="备注">
              <el-input v-model.trim="detailObject.remark" maxlength="64" show-word-limit></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="任务参数">
          <el-input type="textarea" :rows="3" v-model.trim="detailObject.taskParameter"></el-input>
        </el-form-item>
      </el-form>

      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="validateObj">确 定</el-button>
      </span>

    </el-dialog>

    <el-dialog :title="runtimeDialogTitle" :visible.sync="runtimeDialogVisible" width="90%" @close="runtimeDialogClose">
      <el-table ref="singleTable" :data="runtimeList" highlight-current-row border style="width: 100%" v-loading="runtimeTableLoading" size="mini" :header-cell-style="{background:'#F3F4F7',color:'#555'}" row-key='id'>
        <el-table-column type="expand">
          <template slot-scope="props">
            <el-form label-position="left" inline class="demo-table-expand">
              <el-form-item label="任务更新时间">
                <span>{{ dateFormatVal(props.row.taskDateUpdated) }}</span>
              </el-form-item>
              <el-form-item label="Master版本">
                <span>{{ props.row.masterVersion }}</span>
              </el-form-item>
              <el-form-item label="Worker版本">
                <span>{{ props.row.workerVersion }}</span>
              </el-form-item>
              <el-form-item label="任务项">
                <span>{{ props.row.taskItems }}</span>
              </el-form-item>
              <el-form-item label="启动信息">
                <span>{{ props.row.loadMessage }}</span>
              </el-form-item>
              <el-form-item label="运行信息">
                <span>{{ props.row.runMessage }}</span>
              </el-form-item>
            </el-form>
          </template>
        </el-table-column>
        <el-table-column property="seqNo" label="序号" align="center" width="50"> </el-table-column>
        <el-table-column property="cluster" label="运行集群" align="center" width="100"> </el-table-column>
        <el-table-column property="registerTime" label="注册时间" align="center" width="150" :formatter="dateFormat"> </el-table-column>
        <el-table-column property="masterAddress" label="Master" align="center" min-width="150"> </el-table-column>
        <el-table-column property="workerAddress" label="Worker" align="center" min-width="150"> </el-table-column>
        <!-- <el-table-column property="heartBeatTime" label="最近心跳时间" align="center" width="150" :formatter="dateFormat"> </el-table-column> -->
        <el-table-column property="lastRunDataTime" label="最近一次运行时间" align="center" width="150" :formatter="dateFormat"> </el-table-column>
        <el-table-column property="nextRunStartTime" label="下次开始时间" align="center" width="150" :formatter="dateFormat"> </el-table-column>
         <el-table-column property="loadState" label="启动状态" align="center" width="70">
          <template slot-scope="scope">
            <el-tag v-if='scope.row.loadState==="INIT"' size="mini">初始化</el-tag>
            <el-tag v-if='scope.row.loadState==="START"' type="success" size="mini">已启动</el-tag>
            <el-tag v-if='scope.row.loadState==="WAIT_START"' size="mini">待启动</el-tag>
            <el-tag v-if='scope.row.loadState==="STOP"' type="danger" size="mini">停止</el-tag>
            <el-tag v-if='scope.row.loadState==="ERROR"' type="danger" size="mini">异常</el-tag>
          </template>
        </el-table-column>
        <el-table-column property="runState" label="运行状态" align="center" width="70">
          <template slot-scope="scope">
            <el-tag v-if='scope.row.runState==="INIT"' size="mini">初始化</el-tag>
            <el-tag v-if='scope.row.runState==="RUNNING"' type="success" size="mini">运行中</el-tag>
            <el-tag v-if='scope.row.runState==="WAITING"' type="warning" size="mini">等待中</el-tag>
            <el-tag v-if='scope.row.runState==="SLEEP"'  type="warning" size="mini">睡眠中</el-tag>
            <el-tag v-if='scope.row.runState==="ERROR"' type="danger" size="mini">异常</el-tag>
            <el-tag v-if='scope.row.runState==="DEAD"' type="danger" size="mini">死亡</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <el-drawer :title="'任务['+currentObject.taskName+']信息'" :visible.sync="detailShow" direction="ttb" class="expandDrawer" size='300'>
      <el-form :inline="true" style='padding:0px 30px'>
        <el-row>
          <el-col :span="6">
            <el-form-item label="所属服务">
              {{currentObject.dealSysCode}}
            </el-form-item>
          </el-col>
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
            <el-form-item label="执行开始">
              {{currentObject.permitRunStartTime}}
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
            <el-form-item label="每次获取数量">
              {{currentObject.fetchDataNumber}}
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="每次执行数量">
              {{currentObject.executeNumber}}
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
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
           <el-col :span="6">
            <el-form-item label="任务项数组">
              {{currentObject.taskItems}}
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
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
import CronInput from '../../components/cron/cron-input'

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
        processorType: '',
        forceCronExec: '',
        taskName: '',
        dealBizBeanName: '',
        permitRunStartTime: '',
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
      taskRuntimeTime: '',
      rule: [{ required: true, message: '不能为空！', trigger: 'blur,change' }]
    }
  },
  mounted () {
    this.loadAllSysCodes()
    this.onQuery()
    this.loadAllCluster()
    this.loadAllTagCodes()
  },
  methods: {
    changeCron: function (val) {
      this.$set(this.detailObject, 'permitRunStartTime', val)
    },
    dateFormat: function (row, column) {
      var date = row[column.property]
      if (date === undefined || !date) {
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
      this.$http
        .post('/api/task/getAll', this.queryObj)
        .then(res => {
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
        })
        .catch(errors => {
          this.dataList = []
          this.$message.error(errors.msg)
          this.tableLoading = false
        })
    },
    newObject () {
      this.detailObject = {}
      this.$set(this.detailObject, 'processorType', 'Execute')
      this.$set(this.detailObject, 'heartBeatRate', 5)
      this.$set(this.detailObject, 'sleepTimeNoData', 0)
      this.$set(this.detailObject, 'sleepTimeInterval', 0)
      this.$set(this.detailObject, 'permitRunStartTime', '0 0/1 * * * ?')
      if (this.allTagCodes && this.allTagCodes.length === 1) {
        this.$set(this.detailObject, 'tag', this.allTagCodes[0])
      }
      if (this.clusterList && this.clusterList.length === 1) {
        this.$set(this.detailObject, 'cluster', this.clusterList[0].cluster)
      }
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
      this.runtimeDialogVisible = true
      this.runtimeDialogTitle = '[' + row.taskName + ']运行信息(3分钟后自动关闭)'
      this.runtimeList = []
      let _this = this
      if (this.taskRuntimeTime) {
        clearInterval(this.taskRuntimeTime)
      }
      this.$http
        .post('/api/task/taskRuntime', data)
        .then(res => {
          this.runtimeList = res
          this.runtimeTableLoading = false

          this.taskRuntimeTime = setInterval(() => {
            _this.$http
              .post('/api/task/taskRuntime', data)
              .then(res2 => {
                _this.runtimeList = res2
              })
              .catch(errors => {
                _this.$message.error(errors.msg)
              })
          }, 1000)
        })
        .catch(errors => {
          this.$message.error(errors.msg)
          this.runtimeTableLoading = false
        })

      setTimeout(() => {
        _this.runtimeDialogVisible = false
      }, 1000 * 60 * 3)
    },
    runtimeDialogClose () {
      if (this.taskRuntimeTime) {
        clearInterval(this.taskRuntimeTime)
      }
    },
    validateObj () {
      this.$refs['objForm'].validate(valid => {
        if (valid) {
          this.save()
        } else {
          this.$message.error('请检查，存在不符合规范的输入！')
          return false
        }
      })
    },
    save () {
      var url = ''
      if (this.EditType === 'new' || this.EditType === 'copy') {
        url = '/api/task/'
        this.detailObject['state'] = 'STOP'
        this.$http
          .post(url, this.detailObject)
          .then(res => {
            this.dialogVisible = false
            this.$message.success({
              duration: 3 * 1000,
              showClose:true,
              offset:80,
              message:'操作成功！'
            })
            this.onQuery()
          })
          .catch(errors => {
            this.$message.error(errors.msg)
          })
      } else {
        url = '/api/task/'
        this.$http
          .put(url, this.detailObject)
          .then(res => {
            this.dialogVisible = false
            this.$message.success({
              duration: 3 * 1000,
              showClose:true,
              offset:80,
              message:'操作成功！'
            })
            this.onQuery()
          })
          .catch(errors => {
            this.$message.error(errors.msg)
          })
      }
    },
    deleteObject (row) {
      if (row.state === 'START') {
        this.dialogVisible = false
        this.$message({
          message: '删除失败！先停止任务再删除！',
          duration: 3 * 1000,
          showClose:true,
          offset:80,
          type: 'error'
        })
        return
      }
      this.$http
        .delete('/api/task/' + row.cluster + '/' + row.dealSysCode + '/' + row.taskName)
        .then(res => {
          this.dialogVisible = false
          this.$message({
            message: '删除成功！',
            duration: 3 * 1000,
            showClose:true,
            offset:80,
            type: 'success'
          })
          this.onQuery()
        })
        .catch(errors => {
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
    stopTask (row) {
      this.$http
        .put('/api/task/stop/' + row.cluster + '/' + row.dealSysCode + '/' + row.taskName, {})
        .then(res => {
          this.dialogVisible = false
          this.$message.success({
            duration: 3 * 1000,
            showClose:true,
            offset:80,
            message:'操作成功！'
          })
          this.refreshRuntimeState(row, 'STOP')
        })
        .catch(errors => {
          this.$message.error(errors.msg)
        })
    },
    startTask (row) {
      this.$http
        .put('/api/task/start/' + row.cluster + '/' + row.dealSysCode + '/' + row.taskName, {})
        .then(res => {
          this.dialogVisible = false
          this.$message.success({
            duration: 3 * 1000,
            showClose:true,
            offset:80,
            message:'操作成功！'
          })
          this.refreshRuntimeState(row, 'START')
        })
        .catch(errors => {
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
        _this.$http
          .post('/api/task/taskRuntime', data)
          .then(res => {
            if (
              (state === 'STOP' && res.length === 0) ||
              (state === 'START' && assignNum === res.length)
            ) {
              let failCount = 0
              let succCount = 0
              for (var i = 0; i < res.length; i++) {
                if (res[i].state === 'ERROR') {
                  failCount = failCount + 1
                } else {
                  succCount = succCount + 1
                }
              }
              _this.taskChangeResultMap[id + 'succCount'] = succCount
              _this.taskChangeResultMap[id + 'failCount'] = failCount
              _this.taskChangeStateMap[id] = false
              window.clearInterval(time)
              _this.onQuery()
            }
            _this.runtimeList = res
          })
          .catch(errors => {
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
  components: {
    CronInput
  }
}
</script>

<style scoped>
.tableHeader {
  background-color: red !important;
}
.expandDrawer .el-form-item {
  margin-bottom: 0px;
  height: 40px;
  width: 100%;
}

.demo-table-expand {
  font-size: 0;
}
.demo-table-expand label {
  width: 90px;
  color: #99a9bf;
}
.demo-table-expand .el-form-item {
  margin-right: 0;
  margin-bottom: 0;
  width: 50%;
}
.cell-div {
  margin-bottom: 10px;
}
</style>
