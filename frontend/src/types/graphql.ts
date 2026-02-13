export type Player = {
  id: string;
  name: string;
  category: "BAT" | "BOWL" | "AR" | "WKB";
};

export type Team = {
  id: string;
  name: string;
};

export type AuctionPlayer = {
  id: string;
  status: "LIVE" | "NOT_STARTED" | "SOLD" | "UNSOLD";
  basePrice: number;
  currentPrice: number;
  player: Player;
  currentHighestBidTeam: Team | null;
  timerStartAt?: string; // ISO 8601 timestamp
  timerEndAt?: string; // ISO 8601 timestamp
  adminPaused: boolean;
  adminPausedAt?: string;
  timerRemainingOnPause?: number;
};

export type GetAuctionPlayersResponse = {
  auctionPlayers: AuctionPlayer[];
};

export type Bid = {
  id: string;
  amount: number;
  createdAt: string;
  team: {
    id: string;
    name: string;
  };
};

export type GetBidsResponse = {
  bids: Bid[];
};

export type PlaceBidResponse = {
  placeBid: AuctionPlayer;
};

export type WaitRequest = {
  id: string;
  adminWait: boolean;
  createdAt: string;
};

export type UseWaitResponse = {
  useWait: WaitRequest;
};

export type AdminPauseResponse = {
  adminPause: AuctionPlayer;
};

export type AdminResumeResponse = {
  adminResume: AuctionPlayer;
};

export type TeamWaitCountResponse = {
  teamWaitCount: number;
};
