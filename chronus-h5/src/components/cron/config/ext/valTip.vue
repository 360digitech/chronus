<template>
  <div class="cell-div">
    {{ $t('common.valTip') }}<span>{{ val }}</span>
  </div>
</template>

<script>
export default {
  props: {
    val: {
      type: String,
      default: null
    }
  }
}
</script>
