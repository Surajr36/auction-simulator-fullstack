import { gql } from "@apollo/client";
export const GET_AUCTION_PLAYERS = gql`
  query GetAuctionPlayers($auctionId: ID!) {
    auctionPlayers(auctionId: $auctionId) {
      id
      status
      basePrice
      currentPrice
      timerStartAt
      timerEndAt
      adminPaused
      adminPausedAt
      timerRemainingOnPause
      player {
        id
        name
        category
      }
      currentHighestBidTeam {
        id
        name
      }
    }
  }
`;

export const GET_BIDS_FOR_AUCTION_PLAYER = gql`
  query GetBidsForAuctionPlayer($auctionPlayerId: ID!) {
    bids(auctionPlayerId: $auctionPlayerId) {
      id
      amount
      createdAt
      team {
        id
        name
      }
    }
  }
`;

export const PLACE_BID = gql`
  mutation PlaceBid($auctionPlayerId: ID!, $amount: Float!) {
    placeBid(auctionPlayerId: $auctionPlayerId, amount: $amount) {
      id
      currentPrice
      status
      timerEndAt
      adminPaused
      currentHighestBidTeam {
        id
        name
      }
    }
  }
`;

export const USE_WAIT = gql`
  mutation UseWait($auctionPlayerId: ID!, $teamId: ID!) {
    useWait(input: { auctionPlayerId: $auctionPlayerId, teamId: $teamId }) {
      id
      adminWait
      createdAt
    }
  }
`;

export const ADMIN_PAUSE = gql`
  mutation AdminPause($auctionPlayerId: ID!) {
    adminPause(auctionPlayerId: $auctionPlayerId) {
      id
      adminPaused
      adminPausedAt
      timerRemainingOnPause
      timerEndAt
    }
  }
`;

export const ADMIN_RESUME = gql`
  mutation AdminResume($auctionPlayerId: ID!) {
    adminResume(auctionPlayerId: $auctionPlayerId) {
      id
      adminPaused
      timerEndAt
    }
  }
`;

export const GET_TEAM_WAIT_COUNT = gql`
  query GetTeamWaitCount($auctionPlayerId: ID!, $teamId: ID!) {
    teamWaitCount(auctionPlayerId: $auctionPlayerId, teamId: $teamId)
  }
`;
