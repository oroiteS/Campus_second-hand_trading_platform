import axios from "axios";

// 商品相关API
const API_BASE_URL = 'http://localhost:8087';

/**
 * 获取最新发布的商品
 * @returns {Promise} 返回最新6个商品的数据
 */
export const getLatestCommodities = async () => {
  try {
    const response = await fetch(`${API_BASE_URL}/api/commodities/latest`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      },
    });
    
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    
    const result = await response.json();
    
    if (result.code === 200) {
      return result.data;
    } else {
      throw new Error(result.message || '获取商品数据失败');
    }
  } catch (error) {
    console.error('获取最新商品失败:', error);
    throw error;
  }
};

/**
 * 获取所有用户信息
 * @returns {Promise} 返回所有用户数据
 */
export const getAllUsers = async () => {
  try {
    const response = await fetch(`${API_BASE_URL}/api/users/all`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      },
    });
    
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    
    const result = await response.json();
    
    if (result.code === 200) {
      return result.data;
    } else {
      throw new Error(result.message || '获取用户数据失败');
    }
  } catch (error) {
    console.error('获取用户信息失败:', error);
    throw error;
  }
};

/**
 * 将后端商品数据转换为前端显示格式
 * @param {Array} commodities 后端商品数据
 * @param {Array} users 用户数据（可选，用于获取卖家信息）
 * @returns {Array} 转换后的商品数据
 */
export const transformCommodityData = (commodities, users = []) => {
  return commodities.map(item => {
    // 查找对应的卖家信息
    const seller = users.find(user => user.userId === item.sellerId) || {};
    
    // 解析图片列表
    let imageList = [];
    try {
      if (item.imageList) {
        imageList = JSON.parse(item.imageList);
      }
    } catch (e) {
      console.warn('解析图片列表失败:', e);
    }
    
    // 计算发布时间距离现在的时间
    const timeAgo = calculateTimeAgo(item.createdAt);
    
    return {
      id: item.commodityId,
      name: item.commodityName,
      price: item.currentPrice,
      condition: item.newness || '成色良好', // 直接使用后端返回的newness字段
      location: extractLocation(item.commodityDescription),
      image: imageList.length > 0 ? imageList[0] : (item.mainImageUrl || '/测试图片.jpg'),
      timeAgo: timeAgo,
      sellerName: seller.userName || '未知用户',
      sellerSchool: extractSchool(seller.userName) || '未知学院',
      sellerAvatar: seller.avatarUrl || `https://via.placeholder.com/30x30/4CAF50/FFFFFF?text=${seller.userName?.charAt(0) || 'U'}`,
      description: item.commodityDescription,
      status: item.commodityStatus,
      quantity: item.quantity,
      createdAt: item.createdAt,
      updatedAt: item.updatedAt
    };
  });
};

/**
 * 计算时间差
 * @param {string} dateTime 时间字符串
 * @returns {string} 时间差描述
 */
function calculateTimeAgo(dateTime) {
  const now = new Date();
  const past = new Date(dateTime);
  const diffMs = now - past;
  const diffMins = Math.floor(diffMs / 60000);
  const diffHours = Math.floor(diffMs / 3600000);
  const diffDays = Math.floor(diffMs / 86400000);
  
  if (diffMins < 1) {
    return '刚刚';
  } else if (diffMins < 60) {
    return `${diffMins}分钟前`;
  } else if (diffHours < 24) {
    return `${diffHours}小时前`;
  } else {
    return `${diffDays}天前`;
  }
}

/**
 * 从描述中提取位置信息
 * @param {string} description 商品描述
 * @returns {string} 位置信息
 */
function extractLocation(description) {
  const locationPatterns = [
    /(东校区|西校区|南校区|北校区|中心校区)/,
    /(宿舍|图书馆|食堂|教学楼)/,
    /([东西南北]区)/
  ];
  
  for (const pattern of locationPatterns) {
    const match = description.match(pattern);
    if (match) {
      return match[1];
    }
  }
  
  return '校内';
}

/**
 * 从用户名中提取学院信息（这里是示例，实际可能需要其他方式）
 * @param {string} userName 用户名
 * @returns {string} 学院信息
 */
// eslint-disable-next-line no-unused-vars
function extractSchool(userName) {
  // 这里可以根据实际情况调整学院提取逻辑
  const schools = [
    '计算机学院', '数学学院', '物理学院', '化学学院', 
    '生物学院', '外语学院', '文学院', '历史学院',
    '音乐学院', '美术学院', '体育学院', '经济学院'
  ];
  
  return schools[Math.floor(Math.random() * schools.length)];
}

/**
 * 获取商品详情
 * @param {string} commodityId 商品ID
 * @returns {Promise} 返回商品详情数据
 */
export const getCommodityDetail = async (commodityId) => {
  try {
    const response = await fetch(`http://localhost:8083/api/commodity/detail/${commodityId}`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      },
    });
    
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    
    const result = await response.json();
    
    if (result.code === 200) {
      return result.data;
    } else {
      throw new Error(result.message || '获取商品详情失败');
    }
  } catch (error) {
    console.error('获取商品详情失败:', error);
    throw error;
  }
};

/**
 * 将后端商品详情数据转换为前端显示格式
 * @param {Object} commodityData 后端商品详情数据
 * @returns {Object} 转换后的商品数据
 */
export const transformCommodityDetailData = async (commodityData) => {
  // 解析图片列表
  let imageList = [];
  try {
    if (commodityData.imageList) {
      // 如果imageList已经是数组，直接使用；如果是字符串，则解析
      if (Array.isArray(commodityData.imageList)) {
        imageList = commodityData.imageList.map(url => {
          // 清理URL中的反引号和多余空格
          return typeof url === 'string' ? url.replace(/`/g, '').trim() : url;
        });
      } else if (typeof commodityData.imageList === 'string') {
        // 尝试解析JSON字符串
        try {
          const parsed = JSON.parse(commodityData.imageList);
          imageList = Array.isArray(parsed) ? parsed.map(url => 
            typeof url === 'string' ? url.replace(/`/g, '').trim() : url
          ) : [];
        } catch (parseError) {
          // 如果JSON解析失败，尝试按逗号分割
          imageList = commodityData.imageList.split(',').map(url => 
            url.replace(/`/g, '').trim()
          ).filter(url => url.length > 0);
        }
      }
    }
  } catch (e) {
    console.warn('解析图片列表失败:', e);
    imageList = [];
  }
  
  // 构建图片数组，主图放在第一位
  const images = [];
  if (commodityData.mainImageUrl) {
    // 清理主图URL中的反引号和多余空格
    const cleanMainImageUrl = commodityData.mainImageUrl.replace(/`/g, '').trim();
    if (cleanMainImageUrl) {
      images.push(cleanMainImageUrl);
    }
  }
  
  // 添加所有图片，包括重复的
  imageList.forEach(img => {
    const cleanImg = typeof img === 'string' ? img.replace(/`/g, '').trim() : img;
    if (cleanImg) {
      images.push(cleanImg);
    }
  });
  
  // 如果没有图片，使用默认图片
  if (images.length === 0) {
    images.push('/测试图片.jpg');
  }
  
  // 格式化发布时间
  const formatPublishTime = (dateTime) => {
    const date = new Date(dateTime);
    return date.toLocaleDateString('zh-CN', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  };
  
  // 转换商品状态显示文本
  const getStatusText = (status) => {
    const statusMap = {
      'on_sale': '在售',
      'sold': '已售',
      'off_sale': '下架'
    };
    return statusMap[status] || '未知状态';
  };
  
  // 获取卖家信息
  let sellerInfo = {
    id: commodityData.sellerId,
    name: '卖家用户',
    avatar: 'https://via.placeholder.com/60x60/4CAF50/FFFFFF?text=卖'
  };
  
  try {
    if (commodityData.sellerId) {
      const userInfo = await getUserInfo(commodityData.sellerId);
      sellerInfo = {
        id: userInfo.userId,
        name: userInfo.userName,
        avatar: userInfo.avatarUrl || 'https://via.placeholder.com/60x60/4CAF50/FFFFFF?text=卖'
      };
    }
  } catch (error) {
    console.warn('获取卖家信息失败，使用默认信息:', error);
  }
  
  return {
    id: commodityData.commodityId,
    name: commodityData.commodityName,
    price: commodityData.currentPrice,
    condition: commodityData.newness || '成色良好',
    publishTime: formatPublishTime(commodityData.createdAt),
    quantity: commodityData.quantity || 1,
    description: commodityData.commodityDescription || '',
    detailDescription: commodityData.commodityDescription ? 
      commodityData.commodityDescription.split('\n').filter(line => line.trim()) : 
      ['暂无详细描述'],
    images: images,
    status: getStatusText(commodityData.commodityStatus),
    sellerId: commodityData.sellerId,
    categoryName: commodityData.categoryName,
    createdAt: commodityData.createdAt,
    updatedAt: commodityData.updatedAt,
    // 卖家信息（从API获取）
    seller: sellerInfo
  };
};

/**
 * 获取用户信息
 * @param {string} userId 用户ID
 * @returns {Promise} 返回用户信息数据
 */
export const getUserInfo = async (userId) => {
  try {
    const response = await fetch('http://localhost:8089/api/user/info', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        userId: userId
      })
    });
    
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    
    const result = await response.json();
    
    if (result.code === 200) {
      return result.data;
    } else {
      throw new Error(result.message || '获取用户信息失败');
    }
  } catch (error) {
    console.error('获取用户信息失败:', error);
    throw error;
  }
};

/**
 * 获取卖家商品列表
 * @param {string} sellerId 卖家ID
 * @param {number} page 页码（从1开始）
 * @param {number} size 每页数量
 * @returns {Promise} 返回商品列表数据
 */
export const getSellerProducts = async (sellerId, page = 1, size = 6) => {
  // 默认每页6个商品
  try {
    const response = await fetch(`http://localhost:8084/api/commodity/list/${sellerId}?page=${page}&size=${size}`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      },
    });
    
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    
    const result = await response.json();
    
    if (result.success) {
      return {
        products: result.data.map(item => ({
          id: item.commodityId,
          title: item.commodityName,
          price: item.currentPrice,
          imageUrl: item.mainImageUrl || '/public/测试图片.jpg',
          status: item.commodityStatus,
          condition: item.newness || '成色良好',
          description: item.commodityDescription
        })),
        total: result.total || result.data.length
      };
    } else {
      throw new Error(result.message || '获取商品列表失败');
    }
  } catch (error) {
    console.error('获取卖家商品列表失败:', error);
    throw error;
  }
};
/***  
 * 获取热门推荐的商品 
 * @param {string} commodity_name 商品名称
 * @param {string} commodity_description 商品描述
 * @param {int}category_id 商品分类id
 * @param {list[int]} tags_id 标签列表
 * @param {string} current_price 现在的商品价格，前端要转换为float类型
 * @param {string} commodity_status 商品状态
 * @param {string} main_image_url 图片主图链接
 * @param {list[string]} image_list 图片列表信息
 * @param {int} quantity 商品数量
 * @param {string} newness 商品新旧程度
 * @param {string} commodity_id 商品id
 * @param {dateTime} created_at 商品创建时间
 * @param {dateTime} updated_at 商品更新时间
 * @param {string} user_name
 * @param {string} avatar_url
 * */
export const get_commodities_recommendation = async (user_id) => {
  if (!user_id) {
    return [];
  }
  try {
    const response = await axios.get(`http://localhost:8000/api/v1/commodities/recommendation/${user_id}`);
    if (response.data) {
      return response.data;
    } else {
      return [];
    }
  } catch (error) {
    console.error('获取推荐商品失败:', error);
    return [];
  }
};