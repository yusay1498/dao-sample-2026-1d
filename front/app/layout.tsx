import type { Metadata } from "next";
import { Geist_Mono, Zen_Kaku_Gothic_New } from "next/font/google";
import { Toaster } from "sonner";
import { AuroraBackground } from "@/components/layout/AuroraBackground";
import { Header } from "@/components/layout/Header";
import "./globals.css";

const zenKaku = Zen_Kaku_Gothic_New({
  variable: "--font-zen-kaku",
  subsets: ["latin"],
  weight: ["400", "500", "700", "900"],
});

const geistMono = Geist_Mono({
  variable: "--font-geist-mono",
  subsets: ["latin"],
});

export const metadata: Metadata = {
  title: "Stellar Events | イベント管理",
  description: "夜空にきらめくイベントを見つけて、つくる。",
};

export default function RootLayout({ children }: LayoutProps<"/">) {
  return (
    <html
      lang="ja"
      className={`${zenKaku.variable} ${geistMono.variable} h-full antialiased`}
    >
      <body className="min-h-full flex flex-col">
        <AuroraBackground />
        <Header />
        <main className="mx-auto flex w-full max-w-6xl flex-1 flex-col px-6 py-10">
          {children}
        </main>
        <Toaster theme="dark" position="top-center" richColors />
      </body>
    </html>
  );
}
