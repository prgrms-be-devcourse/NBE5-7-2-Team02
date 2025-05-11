import CardList from "./components/CardList.tsx";
import {NavBar} from "./components/NavBar.tsx";
import {TagForm} from "./components/TagForm.tsx";
import {PreBoardForm} from "./components/PreBoardForm.tsx";

function App() {
  return (
      <div className="min-h-screen bg-bright dark:bg-dark">
        <div className="pt-16">
          <NavBar />
        </div>
        <div className="p-4 max-w-3xl mx-auto">
          <PreBoardForm />
          <div className="pt-4">
            <TagForm />
          </div>
          <div className="pt-4">
            <CardList />
          </div>
        </div>
      </div>
  );
}

export default App;