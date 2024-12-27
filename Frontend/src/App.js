import { Route, BrowserRouter as Router, Routes } from "react-router-dom"; // Use BrowserRouter as Router
import AboutPage from "./Component/Main/About";
import ContactPage from "./Component/Main/contact";
import EntryPage from "./Component/Main/EntryPage";
import Login from "./Component/Main/Login";
import Signup from "./Component/Main/Signup";
import AddRecipe from "./Component/Welcome/AddRecipe";
import Diagram from './Component/Welcome/Diagram';
import EditRecipe from "./Component/Welcome/EditRecipe";
import MainPage from "./Component/Welcome/MainPage";
import Myrecipe from "./Component/Welcome/Myrecipe";
import Profile from "./Component/Welcome/Profile";
import Recipe from "./Component/Welcome/Recipe";
function App() {
  return (
    <div className="App">
      <Router>
        <Routes>
          <Route path="/" element={<EntryPage />} />
          <Route path="/contact" element={<ContactPage />} />
          <Route path="/about" element={<AboutPage />} />
          <Route path="/login" element={<Login />} />
          <Route path="/signup" element={<Signup />} />
        </Routes>
        <Routes>
          <Route path="/welcome" element={<MainPage/>}/>
          <Route path="/welcome/contact" element={<ContactPage/>} />
          <Route path="/welcome/about" element={<AboutPage/>} />
          <Route path="/profile" element={<Profile/>}/>
          <Route path="/recipe/:id" element={<Recipe/>}/>
          <Route path="/diagram/:id" element={<Diagram/>}/>
          <Route path="/add-recipe" element={<AddRecipe/>}/>
          <Route path="/my-recipe" element={<Myrecipe/>}/>
          <Route path="/edit-recipe/:id" element={<EditRecipe/>}/>
        </Routes>
      </Router>
    </div>
  );
}

export default App;
