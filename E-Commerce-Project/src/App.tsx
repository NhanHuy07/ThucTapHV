
import { Routes, Route } from 'react-router-dom'
import { ToastContainer } from 'react-toastify'
import 'react-toastify/dist/ReactToastify.css'

import Home from './pages/Home';
import About from './pages/About';
import Contact from './pages/Contact';
import Login from './pages/Login';
import Orders from './pages/Orders';
import PlaceOrder from './pages/PlaceOrder';
import Product from './pages/Product';
import Cart from './pages/Cart';
import Collection from './pages/Collection';
import TrackOrder from './pages/TrackOrder';
import Profile from './pages/Profile';
import Admin from './pages/Admin';
import PaymentReturn from './pages/PaymentReturn';

import Navbar from './components/Navbar';
import SearchBar from './components/SearchBar';
import Footer from './components/Footer';
import ChatWidget from './components/ChatWidget';
const App = () => {
  return (
    <div className='px-4 sm:px-[5vw] md:px-[7vw] lg:px-[9vw]'>
      <ToastContainer />
      <Navbar />
      <SearchBar />
      <Routes>
        <Route path='/' element={<Home />} />
        <Route path='/about' element={<About />} />
        <Route path='/contact' element={<Contact />} />
        <Route path='/collection' element={<Collection />} />
        <Route path='/cart' element={<Cart />} />
        <Route path='/login' element={<Login />} />
        <Route path='/profile' element={<Profile />} />
        <Route path='/orders' element={<Orders />} />
        <Route path='/orders/:orderCode' element={<TrackOrder />} />
        <Route path='/place-order' element={<PlaceOrder />} />
        <Route path='/payment/vnpay/return' element={<PaymentReturn />} />
        <Route path='/product/:productId' element={<Product />} />
        <Route path='/admin' element={<Admin />} />
      </Routes>
      <Footer />
      <ChatWidget />
    </div>
  )
}

export default App
