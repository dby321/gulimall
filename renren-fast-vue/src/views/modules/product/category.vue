<template>
  <div>
    <el-tree :data="menus" :props="defaultProps"  :expand-on-click-node="false" show-checkbox node-key="catId" :default-expanded-keys="expandKeys">
      <span class="custom-tree-node" slot-scope="{ node, data }">
        <span>{{ node.label }}</span>
        <span>
          <el-button
            type="text"
            size="mini"
            v-if="node.level==1||node.level==2"
            @click="() => append(data)">
            Append
          </el-button>
          <el-button
            type="text"
            size="mini"
            v-if="node.childNodes.length==0"
            @click="() => remove(node, data)">
            Delete
          </el-button>
        </span>
      </span>
    </el-tree>
  </div>
</template>
<script>
export default {
  data () {
    return {
      menus: [],
      defaultProps: {
        children: 'children',
        label: 'name'
      },
      expandKeys: []
    }
  },
  created () {
    this.getMenus()
  },
  methods: {
    getMenus () {
      this.$http({
        url: this.$http.adornUrl('/product/category/list/tree'),
        method: 'get'
      }).then(({data}) => {
        console.log('菜单数据', data)
        this.menus = data.entities
      })
    },
    append (data) {
      console.log('append()', data)
    },

    remove (node, data) {
      console.log('remove()', node, data)
      let ids = [data.catId]
      this.$confirm(`此操作将永久删除该文件${data.name}, 是否继续?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$http({
          url: this.$http.adornUrl('/product/category/delete'),
          method: 'post',
          data: this.$http.adornData(ids, false)
        }).then(({data}) => {
          console.log('删除结果', data)
          this.$message({
            type: 'success',
            message: '删除成功!'
          })
          this.getMenus()
          this.expandKeys = [node.parent.data.catId]
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        })
      })
    }
  }
}
</script>
<style>

</style>
