<template>
  <div style="padding:20px"> 
    <el-table ref="singleTable" :data="dataList" highlight-current-row border style="width: 100%"
      v-loading="tableLoading" size="mini" :header-cell-style="{background:'#F3F4F7',color:'#555'}"
      row-key='id'>
      <el-table-column property="cluster" label="集群标识" min-width="120" align='center'>
      </el-table-column>
      <el-table-column property="clusterDesc" label="描述" align="center" min-width="100">
      </el-table-column>
      <el-table-column property="dateUpdated" label="变更时间" align="center" width="160" :formatter="dateFormat">
      </el-table-column>
      <el-table-column property="updatedBy" label="变更用户" align="center" min-width="100">
      </el-table-column>
      <el-table-column property="operation" fixed="right" label="操作" align="center" width="300">
         <template slot="header" slot-scope="scope">
           <el-button size="mini" type="success" @click="newObject">新建</el-button>
        </template>
        <template slot-scope="scope">
          <el-button size="mini" type="primary" @click.stop="editObject(scope.row)">编辑</el-button>
          <el-button size="mini" type="danger" @click.stop="deleteObject(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="550px">
      <el-form label-position='right' label-width="110px">
        <el-form-item label="集群标识">
          <el-input v-model="currentObject.cluster"></el-input>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="currentObject.clusterDesc"></el-input>
        </el-form-item>
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
      dialogTitle: '新建集群',
      dialogVisible: false,
      EditType: 'new',
      queryObj: {
        
      },
      currentObject: {
        cluster:'',
        clusterDesc:''
      },
      runtimeObj: {},
      dataList: [],
      total: 0,
      totalData: [],
      allSysCodes:[]
    }
  },
  mounted () {
    this.onQuery()
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
      this.$http.get('/api/cluster/getAllCluster').then(res => {
        this.tableLoading = false
        this.dataList = res
      })
    },
    newObject () {
      this.dialogTitle = '新建集群'
      this.dialogVisible = true
      this.EditType = 'new'
    },
    editObject (row) {
      this.dialogTitle = '编辑集群'
      this.dialogVisible = true
      this.EditType = 'edit'
      this.currentObject = {}
      this.currentObject = JSON.parse(JSON.stringify(row))
    },
    save () {
      var url = ''
      if (this.EditType === 'new') {
        url = '/api/cluster/'
        this.$http.post(url, this.currentObject).then(res => {
          this.dialogVisible = false
          this.$message.success('操作成功！')
          this.onQuery()
        }).catch(errors => {
          this.$message.error(errors.msg)
        })
      } else {
        url = '/api/cluster/'
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
      this.$http.delete('/api/cluster/' + row.cluster).then(res => {
        this.dialogVisible = false
        this.$message.success('操作成功！')
        this.onQuery()
      }).catch(errors => {
        this.$message.error(errors.msg)
      })
    },
    onShow (row) {
      this.currentObject = row
    }
  }
}
</script>

<style scoped>
.tableHeader {
  background-color: red !important;
}
</style>
