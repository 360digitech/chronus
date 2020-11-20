<template>
    <div style="padding:20px">

        <el-form :inline="true" @submit.native.prevent size="medium">
            <el-form-item label="">
                <el-input size="medium" v-model.trim="queryObj.groupName" placeholder="分组名称" clearable style="width:200px"
                          @keyup.enter.native="onQuery"></el-input>
            </el-form-item>
            <el-form-item label="">
                <el-input size="medium" v-model.trim="queryObj.sysCode" placeholder="系统编号" clearable style="width:200px"
                          @keyup.enter.native="onQuery"></el-input>
            </el-form-item>
            <el-form-item>
                <el-button size="medium" type="primary" @click="onQuery">查询</el-button>
                <el-button size="medium" type="success" @click="newObject">新建</el-button>
            </el-form-item>
        </el-form>

        <el-table ref="singleTable" :data="dataList" highlight-current-row border style="width: 100%"
                  v-loading="tableLoading" size="mini" @row-dblclick="onShow"
                  :header-cell-style="{background:'#F3F4F7',color:'#555'}" row-key='id'>
            <el-table-column property="groupName" label="分组编码" width="120" align='center'>
            </el-table-column>
            <el-table-column property="sysCode" label="系统编号" align="center" width="220">
            </el-table-column>
            <el-table-column property="sysDesc" label="系统描述" align="center">
            </el-table-column>
            <el-table-column property="protocolType" label="服务协议类型" align="center">
                <template slot-scope="scope">
                    <span v-text="scope.row.protocolType ? scope.row.protocolType : 'DUBBO(默认)'"></span>
                </template>
            </el-table-column>
            <el-table-column property="dateUpdated" label="更新时间" align="center" width="160" :formatter="dateFormat">
            </el-table-column>
            <el-table-column property="updatedBy" label="更新人" align="center" width="160">
            </el-table-column>
            <el-table-column property="operation" fixed="right" label="操作" align="center" width="150">
                <template slot-scope="scope">
                    <el-button size="mini" type="primary" @click="editObject(scope.row)">编辑</el-button>
                    <el-button size="mini" type="danger" @click="deleteObject(scope.row)">删除</el-button>
                </template>
            </el-table-column>
        </el-table>

        <el-row type="flex" justify="center" style="margin-top:15px;">
            <el-pagination background layout="total, prev, pager, next, jumper" :current-page.sync="queryObj.pageNum"
                           @current-change="onQuery" :total="total" :page-size="queryObj.pageSize">
            </el-pagination>
        </el-row>

        <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="850px">
            <el-form label-position='right' label-width="110px" size="medium">
                <el-form-item label="系统分组" prop="groupName">
                    <el-select v-model="currentObject.groupName" placeholder="请选择" style="width: 100%">
                        <el-option v-for="item in allGroupName" :key="item.groupName" :label="item.groupName"
                                   :value="item.groupName">
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="系统编号">
                    <el-input v-model.trim="currentObject.sysCode" maxlength="64" show-word-limit></el-input>
                </el-form-item>
                <el-form-item label="系统描述">
                    <el-input v-model.trim="currentObject.sysDesc" maxlength="64"></el-input>
                </el-form-item>
                <el-form-item label="服务协议类型" prop="protocolType">
                   <el-select v-model="currentObject.protocolType" placeholder="请选择" style="width: 100%">
                        <el-option key="DUBBO" label="Dubbo服务" value="DUBBO"> </el-option>
                        <el-option key="HTTP" label="Http服务" value="HTTP"> </el-option>
                    </el-select>
                </el-form-item>
                <!-- DUBBO相关 -->
                <template v-if="currentObject.protocolType === 'DUBBO'">
                    <el-form-item label="注册中心地址" prop="registryAddress">
                        <el-input v-model.trim="protocolConfig.registryAddress"></el-input>
                    </el-form-item>
                    <el-form-item label="超时时间(毫秒)" prop="timeout">
                        <el-input v-model.number="protocolConfig.timeout" ></el-input>
                    </el-form-item>
                </template>
                <!-- Http相关 -->
                <template v-if="currentObject.protocolType === 'HTTP'">
                    <el-form-item label="服务地址" prop="serviceUrl">
                        <el-input v-model.trim="protocolConfig.serviceUrl"></el-input>
                    </el-form-item>
                    <el-form-item label="超时时间(毫秒)" prop="timeout">
                        <el-input v-model.number="protocolConfig.timeout"></el-input>
                    </el-form-item>
                     <el-form-item label="请求头">
                         <div v-for="(item,index) in protocolConfig.header" :key="index">
                             <el-col :span="11">
                                 key:<el-input v-model.trim="item.key" maxlength="64" style="width:80%" show-word-limit></el-input>
                             </el-col>
                             <el-col :span="11">
                                 value:<el-input v-model.trim="item.value" maxlength="64" style="width:80%" show-word-limit></el-input>
                             </el-col>
                             <el-col :span="1">
                                <i class="el-icon-plus" @click="headerAdd()"/>
                             </el-col>
                             <el-col :span="1">
                                <i class="el-icon-minus" @click="headerRemove(index)"/>
                             </el-col>
                         </div>
                    </el-form-item>
                </template>
            </el-form>

            <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="save">确 定</el-button>
      </span>
    </el-dialog>
    </div>
</template>

<script>
import moment from 'moment'
export default {
  data () {
    return {
      tableLoading: false,
      dialogBatchShow: false,
      batchOperationFalg: false,
      protocolConfig: {},
      queryObj: {
        sysCode: '',
        groupName: '',
        pageNum: 1,
        pageSize: 10
      },
      currentObject: {},
      dataList: [],
      total: 0,
      totalData: [],
      allGroupName: [],
      dialogVisible: false,
      dialogLoading: false,
      dialogTitle: '',
      EditType: 'new'
    }
  },
  mounted () {
    this.onQuery()
    this.loadAllGroupName()
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
    headerAdd () {
      this.protocolConfig.header.push({'key': '', 'value': ''})
    },
    headerRemove (index) {
      this.protocolConfig.header.splice(index, 1)
    },
    onQuery () {
      this.tableLoading = true
      this.dataList = []
      this.$http.post('/api/system/getAllGroup', this.queryObj).then(res => {
        this.tableLoading = false
        this.dataList = res.list
        this.total = res.total
      })
    },
    newObject () {
      this.dialogTitle = '新建系统分组'
      this.dialogVisible = true
      this.EditType = 'new'
      this.currentObject = {}
      this.protocolConfig = {}
    },
    editObject (row) {
      this.dialogTitle = '编辑系统分组'
      this.EditType = 'edit'
      var tmpRowObj = JSON.parse(JSON.stringify(row))
      var tmpProtocolConfigObj = {}
      if (tmpRowObj.protocolConfig) {
        tmpProtocolConfigObj = JSON.parse(tmpRowObj.protocolConfig)
        var header = []
        if (tmpProtocolConfigObj.header) {
          for (var key in tmpProtocolConfigObj.header) {
            header.push({'key': key, 'value': tmpProtocolConfigObj.header[key]})
          }
        }

        if (header.length === 0) {
          header.push({'key': '', 'value': ''})
        }
        tmpProtocolConfigObj.header = header
      }
      this.protocolConfig = tmpProtocolConfigObj
      this.currentObject = tmpRowObj
      this.$forceUpdate()
      this.dialogVisible = true
    },
    save () {
      var url = ''
      var tmpProtocolConfigObj = this.protocolConfig
      if (this.protocolConfig.header) {
        var header = {}
        this.protocolConfig.header.forEach(item => {
          header[item.key] = item.value
        })
        tmpProtocolConfigObj.header = header
      }
      this.currentObject['protocolConfig'] = JSON.stringify(tmpProtocolConfigObj)
      if (this.EditType === 'new') {
        url = '/api/system/'
        this.$http.post(url, this.currentObject).then(res => {
          this.dialogVisible = false
          this.$message.success('操作成功！')
          this.onQuery()
        }).catch(errors => {
          this.$message.error(errors.msg)
        })
      } else {
        url = '/api/system/'
        this.$http.put(url, this.currentObject).then(res => {
          this.dialogVisible = false
          this.$message.success('操作成功！')
          this.onQuery()
        }).catch(errors => {
          this.$message.error(errors.msg)
        })
      }
    },
    deleteObject (row) {
      this.$http.delete('/api/system/', {data: row}).then(res => {
        this.$message.success('操作成功！')
        this.onQuery()
      }).catch(errors => {
        this.$message.error(errors.msg)
      })
    },
    onShow (row) {
      this.currentObject = row
      this.detailShow = true
    },
    loadAllGroupName () {
      this.$http.post('/api/group/getAll').then(res => {
        this.allGroupName = res
      })
    }
  },
  watch: {
    'currentObject.protocolType': function (newV, oldV) {
      if (newV === 'DUBBO' && !this.protocolConfig.registryAddress) {
        this.protocolConfig = {}
        this.$set(this.protocolConfig, 'registryAddress', 'globalRegistryAddress')
        this.$set(this.protocolConfig, 'timeout', 30000)
      } else if (newV === 'HTTP' && !this.protocolConfig.serviceUrl) {
        this.protocolConfig = {
          'header': [{'key': '', 'value': ''}]
        }
        this.$set(this.protocolConfig, 'serviceUrl', 'globalServiceUrl')
        this.$set(this.protocolConfig, 'timeout', 30000)
      }
    }
  }

}
</script>

<style scoped>
    .tableHeader {
        background-color: red !important;
    }
</style>
