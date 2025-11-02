<script lang="ts" setup>
import { ref, watch } from 'vue'
import { Search } from '@element-plus/icons-vue'

export interface Options {
  label: string
  value: string
}

export interface SearchItem {
  field: string
  label: string
  type: 'input' | 'select' | 'datepicker'
  placeholder?: string
  options?: Options[]
}

interface Props {
  /** 查询参数 */
  searchParam: Record<string, any>
  /** 搜索项配置 */
  searchItems: SearchItem[]
}

const props = defineProps<Props>()
const emits = defineEmits(['queryTableData'])
const formRef = ref()
const formData = ref({ ...props.searchParam })

function query() {
  emits('queryTableData', formData.value)
}

watch(() => props.searchParam, (val) => {
  formData.value = { ...val }
}, { deep: true })
</script>

<template>
  <div>
    <!-- 查询项 -->
    <el-form
      ref="formRef"
      class="form"
      :model="formData"
      @submit.prevent
    >
      <el-form-item
        v-for="item in searchItems"
        :key="item.label"
        :label="item.label"
        :prop="item.field"
        class="form-item"
      >
        <template v-if="item.type === 'input'">
          <el-input
            v-model="formData[`${item.field}`]"
            :placeholder="item.placeholder ?? '输入关键词搜索'"
            :maxlength="12"
            @keyup.enter="query"
          >
            <template #suffix>
              <el-icon
                class="cursor-pointer"
                @click="query"
              >
                <search />
              </el-icon>
            </template>
          </el-input>
        </template>
        <template v-else-if="item.type === 'select'">
          <el-select
            v-model="formData[`${item.field}`]"
            :placeholder="item.placeholder ?? '请选择'"
            :empty-values="[null, undefined]"
            @change="query"
          >
            <el-option
              v-for="option in item.options"
              :key="option.value"
              :label="option.label"
              :value="option.value"
            >
              {{ option.label }}
            </el-option>
          </el-select>
        </template>
        <template v-else-if="item.type === 'datepicker'">
          <el-date-picker
            v-model="formData[`${item.field}`]"
            class="w-270px"
            type="daterange"
            range-separator="—"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            value-format="YYYY-MM-DD"
          />
        </template>
      </el-form-item>
    </el-form>
  </div>
</template>

<style scoped lang="scss">
.form {
  display: flex;
}

.form-item {
  width: 272px;
  margin-right: 30px;
}
</style>
