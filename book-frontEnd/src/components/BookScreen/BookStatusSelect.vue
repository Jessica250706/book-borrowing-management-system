<template>
  <el-select
    v-model="statusValue"
    :placeholder="placeholder"
    @change="handleChange"
    filterable
    clearable
  >
    <el-option
      v-for="item in options"
      :key="item.value"
      :label="item.label"
      :value="item.value"
    />
  </el-select>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue';

interface StatusOption {
  label: string;
  value: string;
}

interface Props {
  options: StatusOption[];
  placeholder?: string;
  modelValue?: string;
}

const props = withDefaults(defineProps<Props>(), {
  options: () => [],
  placeholder: '所有状态',
  modelValue: ''
});

const emits = defineEmits(['change', 'update:modelValue']);

const statusValue = ref(props.modelValue);

const handleChange = (val: string) => {
  emits('change', val);
  emits('update:modelValue', val);
};

// 监听外部值变化
watch(() => props.modelValue, (newVal) => {
  statusValue.value = newVal;
});
</script>

<style scoped>
/* 可根据项目样式需求调整 */
</style>