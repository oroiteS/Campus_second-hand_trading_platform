import userService from './userService';
import { commodityService } from './commodityService';

// 导出所有API服务
export {
  commodityService as productService, // 使用 commodityService 作为 productService 的别名
  userService,
  commodityService
};