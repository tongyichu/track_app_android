// auth.ts 提供微信登录相关的占位实现，实际项目中需集成微信开放平台 SDK。

export const wechatLogin = async (): Promise<string> => {
  // TODO: 集成微信登录 SDK，并在此完成 code 换取 userId 的流程。
  // 这里简单返回一个模拟的用户 ID。
  await new Promise(resolve => setTimeout(resolve, 300));
  return 'demo-user-id';
};
