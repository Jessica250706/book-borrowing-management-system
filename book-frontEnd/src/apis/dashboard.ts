// src/apis/dashboard.ts
// 首页面板接口（包含本地模拟数据）

import axios from 'axios'

// =============================
// 一、数据统计（顶部四项）
// =============================
export async function getStatistics() {
  return Promise.resolve({
    data: {
      dailySales: { amount: 10500, rate: 3.5 },
      dailyGross: { amount: 4200, rate: 2.1 },
      dailyCount: { amount: 86, rate: -1.2 },
      dailyIncome: { amount: 8900, rate: 5.8 }
    }
  })
}

// =============================
// 二、常用功能
// =============================
export async function getFunctions() {
  return Promise.resolve({
    data: [
      { label: '采购订单报表', to: '/features/purchase-report' },
      { label: '应收账款明细表', to: '/features/ar-detail' },
      { label: '条码管理', to: '/features/barcode' },
      { label: '辅助属性', to: '/features/aux-attr' }
    ]
  })
}

// =============================
// 三、汇总信息
// =============================
export async function getSummary() {
  return Promise.resolve({
    data: [
      { label: '商品总数', value: 248, to: '/goods' },
      { label: '客户总数', value: 93, to: '/customers' },
      { label: '供应商总数', value: 27, to: '/suppliers' },
      { label: '库存总数', value: 1674, to: '/stock' },
      { label: '库存预警', value: 12, to: '/warnings/inventory' },
      { label: '保质期预警', value: 5, to: '/warnings/expiry' }
    ]
  })
}

// =============================
// 四、数据概览（前六个折线图）
// =============================
function generateOverviewData(title: string) {
  return {
    data: {
      type: title,
      list: Array.from({ length: 30 }, (_, i) => ({
        date: `2025-09-${(24 + i <= 30 ? 24 + i : 24 + i - 30).toString().padStart(2, '0')}`,
        value: Math.round(Math.random() * 100)
      }))
    }
  }
}

export async function getOverviewPurchase() {
  return Promise.resolve(generateOverviewData('采购单'))
}
export async function getOverviewPurchaseReturn() {
  return Promise.resolve(generateOverviewData('采购退货单'))
}
export async function getOverviewSale() {
  return Promise.resolve(generateOverviewData('销售单'))
}
export async function getOverviewSaleReturn() {
  return Promise.resolve(generateOverviewData('销售退货单'))
}
export async function getOverviewReceipt() {
  return Promise.resolve(generateOverviewData('收款单'))
}
export async function getOverviewPayment() {
  return Promise.resolve(generateOverviewData('付款单'))
}

// =============================
// 五、库存数据（饼图）
// =============================
export async function getOverviewStock() {
  return Promise.resolve({
    data: {
      list: [
        { name: '一号仓', value: 200 },
        { name: '二号仓', value: 340 },
        { name: '三号仓', value: 180 },
        { name: '四号仓', value: 260 },
        { name: '五号仓', value: 120 }
      ]
    }
  })
}

// =============================
// 六、资产数据
// =============================
export async function getAssets() {
  return Promise.resolve({
    data: {
      list: [
        { label: '资金余额', value: '-891元', to: '/assets/balance' },
        { label: '库存成本', value: '6128元', to: '/assets/inventory' },
        { label: '应收余额', value: '-230元', to: '/assets/receivable' },
        { label: '应付余额', value: '100元', to: '/assets/payable' }
      ]
    }
  })
}

// =============================
// 七、资金数据（柱状图）
// =============================
export async function getFunds() {
  return Promise.resolve({
    data: {
      list: [
        { name: '测试账户', value: -1200 },
        { name: '资金测试', value: 400 },
        { name: '备用金', value: 800 },
        { name: '线上账户', value: -300 }
      ]
    }
  })
}
