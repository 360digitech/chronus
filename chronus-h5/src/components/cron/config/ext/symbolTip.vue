<template>
  <div class="cell-div">
    {{ $t('common.symbolTip') }}<span>{{ symbol }}</span>
  </div>
</template>

<script>
export default {
  props: {
    symbol: {
      type: String,
      default: null
    }
  }
}
</script>
