export function AuroraBackground() {
  return (
    <div
      aria-hidden
      className="pointer-events-none fixed inset-0 -z-10 overflow-hidden bg-[#05050f]"
    >
      <div className="aurora-blob-1 absolute -top-40 -left-40 h-[32rem] w-[32rem] rounded-full bg-violet-600/30 blur-3xl" />
      <div className="aurora-blob-2 absolute top-1/3 -right-40 h-[28rem] w-[28rem] rounded-full bg-pink-500/25 blur-3xl" />
      <div className="aurora-blob-3 absolute -bottom-40 left-1/3 h-[30rem] w-[30rem] rounded-full bg-cyan-400/20 blur-3xl" />
      <div className="absolute inset-0 bg-[radial-gradient(circle_at_center,transparent,rgba(5,5,15,0.85))]" />
    </div>
  );
}
