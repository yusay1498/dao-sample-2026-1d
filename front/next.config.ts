import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  // Dockerイメージを軽量化するため、実行に必要な最小限のファイルのみを出力する
  output: "standalone",
};

export default nextConfig;
