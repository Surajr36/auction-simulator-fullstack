import { useQuery } from "@apollo/client/react";
import { GET_AUCTION_PLAYERS } from "./graphql/queries";

function App() {
  const { data, loading, error } = useQuery(GET_AUCTION_PLAYERS, {
    variables: { auctionId: 1 },
  });

  console.log({ data, loading, error });

  if (loading) return <div className="p-6">Loading auction...</div>;
  if (error) return <div className="p-6 text-red-500">{error.message}</div>;

  return (
    <div className="p-6">
      <pre>{JSON.stringify(data, null, 2)}</pre>
    </div>
  );
}

export default App;
